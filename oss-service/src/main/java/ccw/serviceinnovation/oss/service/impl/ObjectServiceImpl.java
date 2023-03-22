package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.constant.FileTypeConstant;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.hash.QETag;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import ccw.serviceinnovation.oss.manager.redis.ChunkRedisService;
import ccw.serviceinnovation.oss.manager.redis.DuplicateRemovalService;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.OssObjectMapper;
import ccw.serviceinnovation.oss.pojo.bo.ChunkBo;
import ccw.serviceinnovation.oss.pojo.bo.GetObjectBo;
import ccw.serviceinnovation.oss.pojo.vo.ObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.OssObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IObjectService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileTypeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.StorageObjectService;
import service.StorageTempObjectService;
import service.raft.client.RaftRpcRequest;

import java.io.IOException;
import java.util.*;

import static ccw.serviceinnovation.oss.constant.OssApplicationConstant.LOCATION_PRODUCTION;

/**
 * @author 陈翔
 */
@Service
@Slf4j
@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class ObjectServiceImpl extends ServiceImpl<OssObjectMapper, OssObject> implements IObjectService {


    /**
     * 服务消费者
     */
    @DubboReference(version = "1.0.0", group = "object")
    private StorageObjectService storageObjectService;

    @DubboReference(version = "1.0.0", group = "temp")
    private StorageTempObjectService storageTempObjectService;

    @Autowired
    private OssObjectMapper ossObjectMapper;

    @Autowired
    private BucketMapper bucketMapper;

    @Autowired
    private ChunkRedisService chunkRedisService;


    @Autowired
    private DuplicateRemovalService duplicateRemovalPrefix;

    @Autowired
    private DuplicateRemovalService duplicateRemovalService;

    @Autowired
    ConsistentHashing consistentHashing;



    @Override
    public Boolean deleteObject(String bucketName, String objectName) throws Exception {
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName,objectName);
        //拿到对象的key
        String objectKey = ossObject.getEtag();
        //删除元数据
        int delete = ossObjectMapper.delete(MPUtil.queryWrapperEq("name", ossObject.getName(), "bucket_id", ossObject.getBucketId()));
        //删除真实数据
        RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,ossObject.getGroupId());
        RaftRpcRequest.del(leader.getCliClientService(), leader.getPeerId(), ossObject.getEtag());
        return true;
    }

    @Override
    public Boolean addSmallObject(String bucketName, String objectName, String etag, MultipartFile file, Long parentObjectId) throws Exception {
        byte[] bytes = file.getBytes();
        //校验客户端etag
        if (Objects.equals(QETag.getETag(bytes), etag)) {
            //检查是否已经存储
            if (duplicateRemovalPrefix.isExist(etag)) {
                return true;
            } else {
                List<Host> allOssDataList = TrackerService.getAllOssDataList(OssApplicationConstant.NACOS_SERVER_ADDR);
                int index = Math.abs(new Random().nextInt());
                Host host = allOssDataList.get(allOssDataList.size() % index);
                String token = UUID.randomUUID().toString();
                UserSpecifiedAddressUtil.setAddress(new Address(host.getIp(), host.getPort(), true));
                storageObjectService.save(bytes, etag);
                Integer type = 0;
//                        storageObjectService.getExt(etag);
                if (type != null) {
                    //保存到redis
                    duplicateRemovalPrefix.saveKey(etag, host.getIp(), host.getPort());
                    //保存到元数据库
                    OssObject ossObject = new OssObject();
                    ossObject.setBucketId(bucketMapper.selectBucketIdByName(bucketName));
                    ossObject.setEtag(etag);
                    ossObject.setParent(parentObjectId);
                    ossObject.setSize(file.getSize());
                    ossObject.setName(objectName);
                    ossObject.setExt(type);
                    ossObjectMapper.insert(ossObject);
                    return true;
                } else {
                    return false;
                }
            }
        } else {
            throw new OssException(ResultCode.FILE_CHECK_ERROR);
        }
    }


    @Override
    public String getBlockToken(String etag, String bucketName, String objectName, Long parentObjectId, Integer chunks, Long size) {
        Bucket bucket = bucketMapper.selectOne(MPUtil.queryWrapperEq("name", bucketName));
        String blockToken = UUID.randomUUID().toString().replace('-', '_');
        System.out.println(blockToken);
        chunkRedisService.saveBlockToken(blockToken, etag, bucket.getUserId(), bucket.getId(), size, parentObjectId, objectName);
        return blockToken;
    }

    @Override
    public Boolean addObjectChunk(MultipartFile file, Integer chunk, String blockToken) throws Exception {
        chunk = chunk + 1;
        log.info("当前为第:{}块分片", chunk);
        ChunkBo chunkBo = chunkRedisService.getObjectPosition(blockToken);
        long size = chunkBo.getSize();
        int chunks = QETag.getChunks(size);
        String etag = chunkBo.getEtag();
        byte[] bytes = file.getBytes();
        //向磁盘服务器存储该分块
        log.info("向{}:{}存储文件块", chunkBo.getIp(), chunkBo.getPort());
        UserSpecifiedAddressUtil.setAddress(new Address(chunkBo.getIp(), chunkBo.getPort(), true));
        storageTempObjectService.saveBlock(blockToken, size, bytes,
                file.getSize(), chunks, chunk);
        //redis保存该分块信息
        String blockSha1 = Base64.getEncoder().encodeToString(QETag.sha1(bytes));
        log.info("第{}块sha1为{}", chunk, blockSha1);
        chunkRedisService.saveChunk(blockToken, chunk, blockSha1);
        return true;
    }

    @Override
    public Boolean mergeObjectChunk(String blockToken) throws Exception {
        ChunkBo chunkBo = chunkRedisService.getObjectPosition(blockToken);
        if(chunkBo==null){
            throw new OssException(ResultCode.UPLOAD_EVENT_EXPIRATION);
        }
        long size = chunkBo.getSize();
        String etag = chunkBo.getEtag();
        String ip = chunkBo.getIp();
        Integer port = chunkBo.getPort();
        String ObjectName = chunkBo.getName();
        OssObject ossObject = new OssObject();
        //文件校验
        if (chunkRedisService.isUploaded(blockToken)) {
            log.info("所有分块上传完毕");
            String finalSha1 = QETag.getFinalSha1(chunkRedisService.getSha1(blockToken));
            log.info("最终的etag:{}", finalSha1);
            if (!finalSha1.equals(etag)) {
                //校验失败
                log.info("校验失败");
                chunkRedisService.removeChunk(blockToken);
                storageTempObjectService.deleteBlockObject(blockToken);
                throw new OssException(ResultCode.CLIENT_ETAG_ERROR);
            } else {
                //校验成功
                log.info("校验成功");
                //去数据库查这个文件是否存在
                OssObject ossObjectByEtag = ossObjectMapper.getOssObjectByEtag(etag);
                //文件去重
                if (ossObjectByEtag != null) {
                    //有重复 -> 删除缓存
                    ossObject.setGroupId(ossObjectByEtag.getGroupId());
                    log.info("文件hash已经存在");
                    LocationVo locationVo = consistentHashing.getObjectNode(etag, ossObjectByEtag.getGroupId());
                    storageTempObjectService.deleteBlockObject(etag);
                } else {
                    log.info("文件不存在");
                    ossObject.setGroupId(chunkBo.getGroupId());
                    //没有重复 -> 转正缓存 (让该group内所有节点去拉取这个缓存,转正的一个过程)
                    //UserSpecifiedAddressUtil.setAddress(new Address(chunkBo.getIp(), chunkBo.getPort(), true));
                    //storageTempObjectService.blockBecomeFullMember(blockToken,etag);
                    RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,chunkBo.getGroupId());
                    String url = "http://" + ip + ":" + port + "/object/download_temp/" + blockToken;
                    LocationVo locationVo = new LocationVo(ip, port);
                    locationVo.setPath(url);
                    locationVo.setToken(blockToken);
                    if (RaftRpcRequest.save(leader.getCliClientService(), leader.getPeerId(), etag, locationVo)) {
                        System.out.println("所有节点完成同步!");
//                        duplicateRemovalService.saveKey(etag, chunkBo.getIp(),chunkBo.getPort());
                    }else{
                        throw new OssException(ResultCode.CANT_SYNC);
                    }
                }
                Long bucketId = chunkBo.getBucketId();
                Long parentObjectId = chunkBo.getParentObjectId();
                // 删除redis相关信息
                chunkRedisService.removeChunk(blockToken);
                // 持久化元数据
                ossObject.setBucketId(bucketId);
                String time = DateUtil.now();
                ossObject.setCreateTime(time);
                ossObject.setLastUpdateTime(time);
                ossObject.setSize(size);
                ossObject.setEtag(etag);
                ossObject.setName(chunkBo.getName());
                //storageObjectService.getExt(etag)
                ossObject.setExt(FileTypeConstant.OTHER);
                ossObject.setParent(parentObjectId);
                Long id = ossObjectMapper.selectObjectIdByIdAndName(bucketId, ObjectName);
                if(id!=null){
                    //存在则更新
                    ossObjectMapper.updateById(ossObject);
                }else{
                    //不存在则插入
                    ossObjectMapper.insert(ossObject);
                }

                return true;
            }
        } else {
            return true;
        }
    }


    @Override
    public OssObject getObjectInfo(String bucketName, String objectName) {
        return ossObjectMapper.selectObjectByName(bucketName, objectName);
    }

    @Override
    public List<OssObjectVo> getObjectList(String bucketName) throws Exception {
        return null;
    }

    @Override
    public Boolean deleteObjects(String bucketName) throws Exception {
        Long bucketId = bucketMapper.selectBucketIdByName(bucketName);
        List<OssObject> objects = ossObjectMapper.selectList(MPUtil.queryWrapperEq("bucket_id", bucketId));
        int count = ossObjectMapper.delete(MPUtil.queryWrapperEq("bucket_id", bucketId));
        for (OssObject ossObject : objects) {
            //拿到对象的key
            String etag = ossObject.getEtag();
            //删除真实数据
            RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,ossObject.getGroupId());
            RaftRpcRequest.del(leader.getCliClientService(), leader.getPeerId(), etag);
            //删除元数据
            ossObjectMapper.delete(MPUtil.queryWrapperEq("name", ossObject.getName(), "bucket_id", ossObject.getBucketId()));

        }
        return true;
    }


    @Override
    public Boolean putFolder(String bucketName, String objectName, Long parentObjectId) {
        return null;
    }

    @Override
    public RPage<ObjectVo> listObjects(String bucketName, Integer pageNum, Integer size, String key, Long parentObjectId,Boolean isImages) {
        Integer offset = null;
        if(pageNum!=null){
            offset = (pageNum-1)*size;
        }
        Integer type = null;
        if(isImages!=null){
            type = FileTypeConstant.IMG;
        }
        List<ObjectVo> list = ossObjectMapper.selectObjectList(bucketName,  offset,  size,  key,parentObjectId,type);
        RPage<ObjectVo> rPage = new RPage<>(pageNum,size,list);
        rPage.setTotalCountAndTotalPage(ossObjectMapper.selectObjectListLength(bucketName, key));
        return rPage;
    }


}

