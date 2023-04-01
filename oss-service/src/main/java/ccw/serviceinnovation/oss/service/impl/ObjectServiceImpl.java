package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.constant.FileTypeConstant;
import ccw.serviceinnovation.common.constant.ObjectACLEnum;
import ccw.serviceinnovation.common.constant.ObjectStateConstant;
import ccw.serviceinnovation.common.constant.StorageTypeEnum;
import ccw.serviceinnovation.common.entity.*;
import ccw.serviceinnovation.common.entity.bo.ColdMqMessage;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.hash.QETag;
import ccw.serviceinnovation.oss.common.InitApplication;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import ccw.serviceinnovation.oss.manager.redis.ChunkRedisService;
import ccw.serviceinnovation.oss.manager.redis.NorDuplicateRemovalService;
import ccw.serviceinnovation.oss.manager.redis.ObjectStateRedisService;
import ccw.serviceinnovation.oss.mapper.*;
import ccw.serviceinnovation.oss.pojo.bo.BlockTokenBo;
import ccw.serviceinnovation.oss.pojo.bo.ChunkBo;
import ccw.serviceinnovation.oss.pojo.vo.ObjectStateVo;
import ccw.serviceinnovation.oss.pojo.vo.ObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.OssObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IObjectService;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.jraft.rhea.storage.StorageType;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.StorageObjectService;
import service.StorageTempObjectService;
import service.bo.FilePrehandleBo;
import service.raft.client.RaftRpcRequest;

import java.nio.charset.StandardCharsets;
import java.util.*;

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
    @DubboReference(version = "1.0.0", group = "object",check = false)
    private StorageObjectService storageObjectService;

    @DubboReference(version = "1.0.0", group = "temp",check = false)
    private StorageTempObjectService storageTempObjectService;

    @Autowired
    private OssObjectMapper ossObjectMapper;

    @Autowired
    private BucketMapper bucketMapper;

    @Autowired
    private ChunkRedisService chunkRedisService;


    @Autowired
    private NorDuplicateRemovalService norDuplicateRemovalService;


    @Autowired
    ColdStorageMapper coldStorageMapper;

    @Autowired
    ObjectStateRedisService objectStateRedisService;

    @Autowired
    BackupMapper backupMapper;

    @Autowired
    ObjectTagMapper objectTagMapper;

    @Autowired
    ObjectTagObjectMapper objectTagObjectMapper;


    @Override
    public ObjectStateVo getState(String bucketName, String objectName) {
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName,objectName);

        return null;
    }

    @Override
    public Boolean backup(String sourceBucketName,String targetBucketName,String objectName,String newObjectName) {
        String backupObjectName;
        if(newObjectName==null){
            backupObjectName = newObjectName;
        }else{
            backupObjectName = "backupData-"+sourceBucketName+"-"+objectName;
        }

        OssObject SourceOssObject = ossObjectMapper.selectObjectByName(sourceBucketName, objectName);
        Long id = bucketMapper.selectBucketIdByName(targetBucketName);
        String etag = SourceOssObject.getEtag();
        //引用次数+1
        String group = norDuplicateRemovalService.getGroup(etag);
        norDuplicateRemovalService.save(etag,group);
        OssObject ossObject = new OssObject();
        ossObject.setIsBackup(true);
        //设置备份名字
        ossObject.setName(backupObjectName);
        ossObject.setStorageLevel(StorageTypeEnum.STANDARD.getCode());
        String time = DateUtil.now();
        ossObject.setCreateTime(time);
        ossObject.setLastUpdateTime(time);
        ossObject.setBucketId(id);
        ossObject.setEtag(etag);
        ossObject.setIsFolder(false);
        ossObject.setObjectAcl(ObjectACLEnum.DEFAULT.getCode());
        ossObject.setParent(null);
        ossObject.setSize(SourceOssObject.getSize());
        ossObject.setSecret(null);
        ossObjectMapper.insert(ossObject);
        Backup backup = new Backup();
        backup.setSourceObjectId(SourceOssObject.getId());
        backup.setTargetObjectId(ossObject.getId());
        backup.setCreateTime(time);
        backupMapper.insert(backup);
        return true;
    }

    @Override
    public Boolean backupRecovery(String bucketName,String objectName) {
        Backup backups = backupMapper.selectBackup(bucketName, objectName);
        Long targetObjectId = backups.getTargetObjectId();
        OssObject sourceOssObject = ossObjectMapper.selectById(backups.getSourceObjectId());
        String sourceGroup = norDuplicateRemovalService.getGroup(sourceOssObject.getEtag());
        OssObject targetOssObject = ossObjectMapper.selectById(targetObjectId);
        String targetGroup = norDuplicateRemovalService.getGroup(targetOssObject.getEtag());
        OssObject ossObject = new OssObject();
        ossObject.setEtag(targetOssObject.getEtag());
        ossObject.setId(backups.getSourceObjectId());
        //改变原来的数据的引用
        ossObjectMapper.updateById(ossObject);
        norDuplicateRemovalService.save(targetOssObject.getEtag(),targetGroup);
        norDuplicateRemovalService.del(sourceOssObject.getEtag());
        return true;
    }


    @Override
    public Boolean deleteObject(String bucketName, String objectName) throws Exception {
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName,objectName);
        String group = norDuplicateRemovalService.getGroup(ossObject.getEtag());
        //删除元数据
        int delete = ossObjectMapper.delete(MPUtil.queryWrapperEq("name", ossObject.getName(), "bucket_id", ossObject.getBucketId()));
        //删除真实数据
        if(norDuplicateRemovalService.del(ossObject.getEtag())==0){
            RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,group);
            RaftRpcRequest.del(leader.getCliClientService(), leader.getPeerId(), ossObject.getEtag());
        }
        return true;
    }

    @Override
    public Boolean addSmallObject(String bucketName, String objectName, String etag, MultipartFile file, Long parentObjectId) throws Exception {
        byte[] bytes = file.getBytes();
        //校验客户端etag
        if (Objects.equals(QETag.getETag(bytes), etag)) {
            //校验成功
            String group = norDuplicateRemovalService.getGroup(etag);
            //检查是否已经存储
            if (group!=null) {
                //已经存储了 则+1
                norDuplicateRemovalService.save(etag, group);
                return true;
            } else {
                String blockToken = UUID.randomUUID().toString().replace('-', '_');
                LocationVo storageObjectNode = ConsistentHashing.getStorageObjectNode(etag);
                UserSpecifiedAddressUtil.setAddress(new Address(storageObjectNode.getIp(), storageObjectNode.getPort(), true));
                storageTempObjectService.saveBlock(blockToken, (long) bytes.length, bytes,
                        file.getSize(), 1, 0);
                RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,storageObjectNode.getGroup());
                String url = "http://" + storageObjectNode.getIp() + ":" +  storageObjectNode.getPort() + "/object/download_temp/" + blockToken;
                LocationVo locationVo = new LocationVo(storageObjectNode.getIp(), storageObjectNode.getPort());
                locationVo.setPath(url);
                locationVo.setToken(blockToken);
                if (RaftRpcRequest.save(leader.getCliClientService(), leader.getPeerId(), etag, locationVo)) {
                    System.out.println("所有节点完成同步!");
                }else{
                    throw new OssException(ResultCode.CANT_SYNC);
                }
                norDuplicateRemovalService.save(etag, storageObjectNode.getGroup());
            }
            return true;
        } else {
            throw new OssException(ResultCode.FILE_CHECK_ERROR);
        }
    }


    @Override
    public BlockTokenBo getBlockToken(String etag, String bucketName, String objectName, Long parentObjectId, Integer chunks, Long size) {
        Bucket bucket = bucketMapper.selectOne(MPUtil.queryWrapperEq("name", bucketName));
        String blockToken = UUID.randomUUID().toString().replace('-', '_');
        System.out.println(blockToken);
        ChunkBo chunkBo = chunkRedisService.saveBlockToken(blockToken, etag, bucket.getUserId(), bucket.getId(), size, parentObjectId, objectName);
        BlockTokenBo blockTokenBo = new BlockTokenBo();
        blockTokenBo.setBlockToken(blockToken);
        blockTokenBo.setIp(chunkBo.getIp());
        blockTokenBo.setPort(chunkBo.getPort());
        return blockTokenBo;
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
        String groupId = chunkBo.getGroupId();
        Integer ossDataProvidePort = TrackerService.getOssDataProvidePort(ip, port);
        if(ossDataProvidePort==null){
            throw new OssException(ResultCode.SERVER_EXCEPTION);
        }
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
                UserSpecifiedAddressUtil.setAddress(new Address(ip,ossDataProvidePort, true));
                storageTempObjectService.deleteBlockObject(blockToken);
                throw new OssException(ResultCode.CLIENT_ETAG_ERROR);
            } else {
                //校验成功
                log.info("校验成功");
                log.info("preHandle:{}:{}",ip,port);
                UserSpecifiedAddressUtil.setAddress(new Address(ip,ossDataProvidePort, true));
                FilePrehandleBo filePrehandleBo = storageTempObjectService.preHandle(blockToken);
                ossObject.setExt(filePrehandleBo.getFileType());
                if(filePrehandleBo.getEtag()!=null){
                    etag = filePrehandleBo.getEtag();
                }
                //去redis查这个文件是否存在
                String group = norDuplicateRemovalService.getGroup(etag);
                //文件去重
                if (group != null) {
                    //有重复 ->  删除缓存
                    //标记次数+1
                    norDuplicateRemovalService.save(etag, group);
                    log.info("文件hash已经存在");
                } else {
                    log.info("文件不存在");
                    RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,chunkBo.getGroupId());
                    String url = "http://" + ip + ":" + port + "/object/download_temp/" + blockToken;
                    LocationVo locationVo = new LocationVo(ip, port);
                    locationVo.setPath(url);
                    locationVo.setToken(blockToken);
                    if (RaftRpcRequest.save(leader.getCliClientService(), leader.getPeerId(), etag, locationVo)) {
                        System.out.println("所有节点完成同步!");
                    }else{
                        throw new OssException(ResultCode.CANT_SYNC);
                    }
                    //标记次数+1
                    norDuplicateRemovalService.save(etag, chunkBo.getGroupId());
                }
                Long bucketId = chunkBo.getBucketId();
                Long parentObjectId = chunkBo.getParentObjectId();
                // 删除redis相关信息
                chunkRedisService.removeChunk(blockToken);
                //删除缓存数据
                storageTempObjectService.deleteBlockObject(blockToken);
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
            throw new OssException(ResultCode.CHUNK_NOT_UP_FINISH);
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
            String group = norDuplicateRemovalService.getGroup(etag);
            long flagCount = norDuplicateRemovalService.del(etag);
            if(flagCount==0){
                //删除真实数据
                RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,group);
                RaftRpcRequest.del(leader.getCliClientService(), leader.getPeerId(), etag);
            }
            //删除元数据
            ossObjectMapper.delete(MPUtil.queryWrapperEq("name", ossObject.getName(), "bucket_id", ossObject.getBucketId()));

            //删除标签
            List<ObjectTagObject> tagObjects = objectTagObjectMapper.selectList(MPUtil.queryWrapperEq("object_id", ossObject.getId()));
            for (ObjectTagObject tagObject : tagObjects) {
                objectTagMapper.delete(MPUtil.queryWrapperEq("id",tagObject.getTagId()));
            }
            objectTagObjectMapper.delete(MPUtil.queryWrapperEq("object_id", ossObject.getId()));
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

    @Override
    public Boolean freeze(String bucketName, String objectName) throws Exception{
        objectStateRedisService.setState(bucketName, objectName, ObjectStateConstant.FREEZE);
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        String etag = ossObject.getEtag();
        if(!ossObject.getStorageLevel().equals(StorageTypeEnum.STANDARD.getCode())){
            throw new OssException(ResultCode.NOT_STANDARD_STORAGE);
        }
        //现在需要进行归档处理,调用cold-data服务将数据下载并压缩保存
        Message msg = new Message("Topic-freeze",
                JSONObject.toJSONString(new ColdMqMessage(ossObject.getId(),etag)).getBytes(StandardCharsets.UTF_8));
        InitApplication.producer.send(msg);
        return true;
    }

    @Override
    public Boolean unfreeze(String bucketName, String objectName)throws Exception {
        objectStateRedisService.setState(bucketName, objectName, ObjectStateConstant.UNFREEZE);
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        String etag = ossObject.getEtag();
        if(ossObject.getStorageLevel().equals(StorageTypeEnum.STANDARD.getCode())){
            throw new OssException(ResultCode.STANDARD_STORAGE);
        }
        //现在需要进行解冻处理,调用cold-data服务将数据下载并解压缩保存
        Message msg = new Message("Topic-unfreeze",
                JSONObject.toJSONString(new ColdMqMessage(ossObject.getId(),etag)).getBytes(StandardCharsets.UTF_8));
        InitApplication.producer.send(msg);
        return true;
    }



}

