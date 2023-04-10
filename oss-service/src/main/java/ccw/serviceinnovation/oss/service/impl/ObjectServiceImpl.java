package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.constant.*;
import ccw.serviceinnovation.common.entity.*;
import ccw.serviceinnovation.common.entity.bo.ColdMqMessage;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.hash.QETag;
import ccw.serviceinnovation.common.util.http.HttpUtils;
import ccw.serviceinnovation.common.util.object.ObjectUtil;
import ccw.serviceinnovation.oss.common.InitApplication;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import ccw.serviceinnovation.oss.manager.redis.ChunkRedisService;
import ccw.serviceinnovation.oss.manager.redis.ColdDuplicateRemovalService;
import ccw.serviceinnovation.oss.manager.redis.NorDuplicateRemovalService;
import ccw.serviceinnovation.oss.manager.redis.ObjectStateRedisService;
import ccw.serviceinnovation.oss.mapper.*;
import ccw.serviceinnovation.oss.pojo.bo.BlockTokenBo;
import ccw.serviceinnovation.oss.pojo.bo.ChunkBo;
import ccw.serviceinnovation.oss.pojo.bo.MqDelTmpBo;
import ccw.serviceinnovation.oss.pojo.dto.BatchDeletionObjectDto;
import ccw.serviceinnovation.oss.pojo.vo.ObjectStateVo;
import ccw.serviceinnovation.oss.pojo.vo.ObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.OssObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IObjectService;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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

    @DubboReference(version = "1.0.0", group = "temp" ,check = false)
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
    private ColdDuplicateRemovalService coldDuplicateRemovalService;


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
        ObjectStateVo objectStateVo = new ObjectStateVo();
        Integer state = objectStateRedisService.getState(bucketName, objectName);
        if(state==null){
            throw new OssException(ResultCode.OBJECT_IS_DEFECT);
        }
        objectStateVo.setState(state);
        objectStateVo.setObjectName(objectName);
        objectStateVo.setBucketName(bucketName);
        if(ObjectStateConstant.FREEZE.equals(state)){
            objectStateVo.setNormal(false);
            objectStateVo.setStateStr("已经归档");
        }if(ObjectStateConstant.NOR.equals(state)){
            objectStateVo.setNormal(true);
            objectStateVo.setStateStr("正常");
        }else if(ObjectStateConstant.UNFREEZING.equals(state)){
            objectStateVo.setNormal(false);
            objectStateVo.setStateStr("解冻中");
        }else if(ObjectStateConstant.FREEZING.equals(state)){
            objectStateVo.setNormal(false);
            objectStateVo.setStateStr("归档中");
        }
        return objectStateVo;
    }

    @Override
    public Boolean backup(String sourceBucketName,String targetBucketName,String objectName,String newObjectName) {
        String backupObjectName;
        if(newObjectName!=null){
            backupObjectName = newObjectName;
        }else{
            //新名字格式为:backupObject-sourceBucket-objectName
            backupObjectName = "backupObject-"+sourceBucketName+"-"+objectName;
        }
        //源对象数据
        OssObject sourceOssObject = ossObjectMapper.selectObjectByName(sourceBucketName, objectName);
        if(!sourceOssObject.getStorageLevel().equals(StorageTypeEnum.STANDARD.getCode())){
            //非标准存储不支持备份
            throw new OssException(ResultCode.CANT_BACKUP_BY_STORAGE);
        }
        //目标桶Id
        Long id = bucketMapper.selectBucketIdByName(targetBucketName);
        //源对象的
        String SourceEtag = sourceOssObject.getEtag();
        //引用次数+1
        String group = norDuplicateRemovalService.getGroup(SourceEtag);
        if(group==null){
            throw new OssException(ResultCode.SYSTEM_ERROR_DATA_NULL);
        }
        norDuplicateRemovalService.save(SourceEtag,group);
        //插入元数据
        OssObject ossObject = new OssObject();
        ossObject.setIsBackup(true);
        //设置备份名字
        ossObject.setName(backupObjectName);
        ossObject.setStorageLevel(StorageTypeEnum.STANDARD.getCode());
        String time = DateUtil.now();
        ossObject.setCreateTime(time);
        ossObject.setLastUpdateTime(time);
        ossObject.setBucketId(id);
        //备份数据域源数据一致
        ossObject.setEtag(SourceEtag);
        ossObject.setIsFolder(false);
        //继承bucket的权限
        ossObject.setObjectAcl(ACLEnum.DEFAULT.getCode());
        ossObject.setParent(null);
        ossObject.setSize(sourceOssObject.getSize());
        ossObject.setSecret(sourceOssObject.getSecret());
        OssObject ossObject1 = ossObjectMapper.selectObjectByName(targetBucketName, backupObjectName);
        if(ossObject1==null){
            ossObjectMapper.insert(ossObject);
        }else{
            //之前的索引-1
            norDuplicateRemovalService.del(ossObject1.getEtag());
            ossObject.setId(ossObject1.getId());
            ossObjectMapper.updateById(ossObject);
        }
        Backup backup = new Backup();
        backup.setSourceObjectId(sourceOssObject.getId());
        backup.setTargetObjectId(ossObject.getId());
        backup.setCreateTime(time);
        backupMapper.insert(backup);
        return true;
    }

    @Override
    public Boolean backupRecovery(String bucketName,String objectName) {
        log.info("{}/{}",bucketName,objectName);
        //只需要一个备份对象即可
        Backup backups = backupMapper.selectBackupByTarget(bucketName, objectName);
        log.info("{}",JSONObject.toJSONString(backups));
        if(backups==null){
            throw new OssException(ResultCode.BACKUP_DATA_NULL);
        }
        Long targetObjectId = backups.getTargetObjectId();
        //拿到源对象
        OssObject sourceOssObject = ossObjectMapper.selectById(backups.getSourceObjectId());
        String sourceGroup = norDuplicateRemovalService.getGroup(sourceOssObject.getEtag());
        //拿到目标对象
        OssObject targetOssObject = ossObjectMapper.selectById(targetObjectId);
        String targetGroup = norDuplicateRemovalService.getGroup(targetOssObject.getEtag());

        if(sourceGroup==null || targetGroup==null){
            throw new OssException(ResultCode.SYSTEM_ERROR_DATA_NULL);
        }
        //改变原来的数据的引用 target+1 source-1
        norDuplicateRemovalService.save(targetOssObject.getEtag(),targetGroup);
        norDuplicateRemovalService.del(sourceOssObject.getEtag());

        //更新源对象
        sourceOssObject.setEtag(targetOssObject.getEtag());
        ossObjectMapper.updateById(sourceOssObject);
        return true;
    }

    @Override
    public Boolean batchDeletion(String bucketName, BatchDeletionObjectDto batchDeletionObjectDto) throws Exception{
        String objectNameListJson = batchDeletionObjectDto.getObjectNameListJson();
        JSONArray objects = JSONObject.parseArray(objectNameListJson);
        for (Object object : objects) {
            if(!deleteObject(bucketName,(String) object)){
                throw new OssException(ResultCode.FILE_DELETE_ERROR);
            }
        }
        return true;
    }


    @Override
    public Boolean deleteObject(String bucketName, String objectName) throws Exception {
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName,objectName);
        if(ossObject==null){
            return true;
        }
        //删除元数据
        ossObjectMapper.deleteById(ossObject.getId());
        //删除对象标签
        List<ObjectTagObject> objectTagObjects = objectTagObjectMapper.selectList(MPUtil.queryWrapperEq("object_id", ossObject.getId()));
        objectTagObjectMapper.delete(MPUtil.queryWrapperEq("object_id",ossObject.getId()));
        for (ObjectTagObject objectTagObject : objectTagObjects) {
            objectTagMapper.delete(MPUtil.queryWrapperEq("id",objectTagObject.getTagId()));
        }
        if(!ossObject.getIsFolder()){
            String group = norDuplicateRemovalService.getGroup(ossObject.getEtag());
            if(ossObject.getStorageLevel().equals(StorageTypeEnum.ARCHIVAL.getCode())){
                //删除真实数据
                if(coldDuplicateRemovalService.del(ossObject.getEtag())==0){
                    String ServerName = coldDuplicateRemovalService.getName(ossObject.getEtag());
                    List<Host> coldList = TrackerService.getColdList(OssApplicationConstant.NACOS_SERVER_ADDR);
                    for (Host host : coldList) {
                        if(host.getMetadata().getCold_storage_name().equals(ServerName)){
                            String url = "http://"+host.getIp()+":"+host.getPort()+"/cold/deleteNor/"+ossObject.getEtag();
                            String delete = HttpUtils.requestTo(url, "DELETE");
                            log.info("{}/{} delete success",bucketName,objectName);
                        }
                    }
                }
                return true;
            }else if(ossObject.getStorageLevel().equals(StorageTypeEnum.STANDARD.getCode())){
                //删除真实数据
                if(norDuplicateRemovalService.del(ossObject.getEtag())==0){
                    RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,group);
                    RaftRpcRequest.del(leader.getCliClientService(), leader.getPeerId(), ossObject.getEtag());
                    log.info("对象{}删除成功",bucketName+"/"+objectName);
                }
            }

        }
        return true;
    }

    @Override
    public Boolean updateObjectName(String bucketName, String objectName, String newName) {
        //查询这个文件名newName是否已经存在
        Long id = ossObjectMapper.selectObjectIdByName(bucketName, newName);
        if(id==null){
            //不存在
            OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
            ossObject.setName(newName);
            ossObjectMapper.updateById(ossObject);
            return true;
        }else{
            //存在
            throw new OssException(ResultCode.NAME_IS_EXIST);
        }
    }

    @Override
    public Boolean updateObjectAcl(String bucketName, String objectName, Integer objectAcl) {
        //查询这个文件名是否已经存在
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        if(ossObject==null){
            //不存在
            throw new OssException(ResultCode.OBJECT_IS_DEFECT);
        }else{
            //存在
            ACLEnum anEnum = ACLEnum.getEnum(objectAcl);
            if(anEnum==null){
                throw new OssException(ResultCode.UNDEFINED);
            }
            ossObject.setObjectAcl(anEnum.getCode());
            ossObjectMapper.updateById(ossObject);
            return true;
        }
    }



    @Override
    public Boolean addSmallObject(String bucketName, String objectName, String etag, MultipartFile file, Long parentObjectId,  Integer objectAcl) throws Exception {
        if(file.getSize() > 5*(1 << 20)){
            //上传
            throw new OssException(ResultCode.FILE_IS_BIG);
        }
        Bucket bucket =bucketMapper.selectBucketByName(bucketName);
        byte[] bytes = file.getBytes();
        //利用一致性hash去寻找存储group
        String blockToken = UUID.randomUUID().toString().replace('-', '_');
        LocationVo storageObjectNode = ConsistentHashing.getStorageObjectNode(etag);
        log.info("LocationVo:{}:{}",storageObjectNode.getIp(),storageObjectNode.getPort());
        //保存到该节点
        Integer providePort = TrackerService.getOssDataProvidePort(storageObjectNode.getIp(),storageObjectNode.getPort());
        UserSpecifiedAddressUtil.setAddress(new Address(storageObjectNode.getIp(),providePort , true));
        storageTempObjectService.saveBlock(blockToken, file.getSize(), bytes,
                file.getSize(), 1, 0,bucket.getSecret());
        //先决事件
        //校验客户端etag
        UserSpecifiedAddressUtil.setAddress(new Address(storageObjectNode.getIp(), providePort, true));
        FilePrehandleBo filePrehandleBo = storageTempObjectService.preHandle(etag, blockToken,false,bucket.getSecret());
        if (filePrehandleBo == null) {
            throw new OssException(ResultCode.FILE_CHECK_ERROR);
        }
        //校验成功
        if (filePrehandleBo.getNewEtag() != null) {
            etag = filePrehandleBo.getNewEtag();
        }
        //-----------持久化元数据-------------
        //插入文件夹
        Long parent = saveFolder(bucket.getId(), objectName, parentObjectId);
        OssObject ossObject = new OssObject();
        String group = norDuplicateRemovalService.getGroup(etag);
        ossObject.setName(objectName);
        ossObject.setSecret(bucket.getSecret());
        ossObject.setParent(parent);
        ossObject.setStorageLevel(StorageTypeEnum.STANDARD.getCode());
        String time = DateUtil.now();
        ossObject.setLastUpdateTime(time);
        ossObject.setCreateTime(time);
        ossObject.setBucketId(bucket.getId());
        ossObject.setEtag(etag);
        ossObject.setExt(filePrehandleBo.getFileType());
        ossObject.setIsFolder(false);
        ossObject.setSize(file.getSize());
        ossObject.setIsBackup(false);
        if(objectAcl!=null){
            ossObject.setObjectAcl(ACLEnum.getEnum(objectAcl).getCode());
        }else{
            ossObject.setObjectAcl(ACLEnum.PRIVATE.getCode());
        }
        OssObject os = ossObjectMapper.selectObjectByName(bucket.getName(), objectName);
        if(os!=null){
            //存在则更新
            ossObject.setId(os.getId());
            norDuplicateRemovalService.del(ossObject.getEtag());
            ossObjectMapper.updateById(ossObject);
        }else{
            //不存在则插入
            ossObjectMapper.insert(ossObject);
        }
        //检查是否已经存储
        if (group != null) {
            //已经存储了 则+1
            norDuplicateRemovalService.save(etag, group);
            return true;
        } else {
            //通过group去找leader
            RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR, storageObjectNode.getGroup());
            //发save请求完成同步
            String url = "http://" + storageObjectNode.getIp() + ":" + storageObjectNode.getPort() + "/object/download_temp/" + blockToken;
            LocationVo locationVo = new LocationVo(storageObjectNode.getIp(), storageObjectNode.getPort());
            locationVo.setPath(url);
            locationVo.setToken(blockToken);
            locationVo.setGroup(storageObjectNode.getGroup());
            if (RaftRpcRequest.save(leader.getCliClientService(), leader.getPeerId(), etag, locationVo)) {
                System.out.println("所有节点完成同步!");
            } else {
                throw new OssException(ResultCode.CANT_SYNC);
            }
            //引用次数+1
            norDuplicateRemovalService.save(etag, storageObjectNode.getGroup());
            //删除缓存
            UserSpecifiedAddressUtil.setAddress(new Address(storageObjectNode.getIp(),providePort, true));
            submitDelTask(new MqDelTmpBo(blockToken,storageObjectNode.getIp(),providePort));
        }
        return true;

    }


    @Override
    public BlockTokenBo getBlockToken(String etag, String bucketName, String objectName, Long parentObjectId,
                                      Integer objectAcl, Integer chunks, Long size) {
        BlockTokenBo blockTokenBo = new BlockTokenBo();
        //先检查文件是否已经存在
        String group = norDuplicateRemovalService.getGroup(etag);
        if(group!=null){
            blockTokenBo.setExist(true);
            return blockTokenBo;
        }
        Bucket bucket = bucketMapper.selectOne(MPUtil.queryWrapperEq("name", bucketName));
        if(bucket==null){
            throw new OssException(ResultCode.BUCKET_IS_DEFECT);
        }
        String blockToken = UUID.randomUUID().toString().replace('-', '_');
        System.out.println("本次创建的blockToken:"+blockToken);
        if(objectAcl==null){
            objectAcl = ACLEnum.DEFAULT.getCode();
        }
        ChunkBo chunkBo = chunkRedisService.saveBlockToken(bucketName,blockToken, etag, bucket.getUserId(), bucket.getId(), size, parentObjectId,bucket.getSecret(),objectAcl, objectName);

        blockTokenBo.setBlockToken(blockToken);
        blockTokenBo.setIp(chunkBo.getIp());
        blockTokenBo.setPort(chunkBo.getPort());
        return blockTokenBo;
    }

    @Override
    public Boolean addObjectChunk(MultipartFile file, Integer chunk, String blockToken,String bucketName) throws Exception {
        chunk = chunk + 1;
        log.info("当前为第:{}块分片", chunk);
        ChunkBo chunkBo = chunkRedisService.getChunkBo(bucketName,blockToken);
        Integer providerPort = TrackerService.getProvidePort(chunkBo.getIp(), chunkBo.getPort());
        long size = chunkBo.getSize();
        int chunks = QETag.getChunks(size);
        String etag = chunkBo.getEtag();
        byte[] bytes = file.getBytes();
        //向磁盘服务器存储该分块
        log.info("向{}:{}存储文件块", chunkBo.getIp(), providerPort);
        UserSpecifiedAddressUtil.setAddress(new Address(chunkBo.getIp(), chunkBo.getPort(), true));
        storageTempObjectService.saveBlock(blockToken, size, bytes,
                file.getSize(), chunks, chunk,chunkBo.getSecret());
        //redis保存该分块信息
        chunkRedisService.saveChunkBit(blockToken, chunk);
        return true;
    }

    /**
     * 插入文件夹
     * @param bucketId
     * @param ossObject
     */
    public void insertFolder(Long bucketId,OssObject ossObject){
        Long id = ossObjectMapper.selectObjectIdByIdAndName(bucketId, ossObject.getName());
        if(id==null){
            ossObjectMapper.insert(ossObject);
        }
    }

    public Long saveFolder(Long bucketId,String objectName,Long parentId){
        if(bucketId==null){
            throw new OssException(ResultCode.BUCKET_IS_DEFECT);
        }
        String[] arr = ObjectUtil.getAllFolder(objectName);
        if(arr!=null){
            StringBuilder prefixName = new StringBuilder();
            if(parentId!=null){
                OssObject ossObject2 = ossObjectMapper.selectById(parentId);
                if(ossObject2==null || !ossObject2.getIsFolder()){
                    throw new OssException(ResultCode.PARENT_ID_IS_INVALID);
                }
                prefixName.append(ossObject2.getName());
            }
            Long prefixId =parentId;
            for (int i = 0; i < arr.length; i++) {
                OssObject ossObject1 = new OssObject();
                ossObject1.setIsFolder(true);
                ossObject1.setParent(prefixId);
                ossObject1.setBucketId(bucketId);
                prefixName = prefixName.append(arr[i]).append("/");
                ossObject1.setName(prefixName.toString());
                ossObject1.setBucketId(bucketId);
                //插入文件夹
                log.info("insert: {}",ossObject1.getName());
                insertFolder(bucketId,ossObject1);
                prefixId = ossObject1.getId();
            }
            return prefixId;
        }
        return null;
    }

    public void submitDelTask(MqDelTmpBo mqDelTmpBo) throws Exception{
        String json = JSONObject.toJSONString(mqDelTmpBo);
        Message msg = new Message(MessageQueueConstant.TOPIC_DELETE_TMP,
                json.getBytes(StandardCharsets.UTF_8));
        InitApplication.producer.send(msg);
        log.info("提交del任务:{}",json);
    }


    @Override
    public Boolean mergeObjectChunk(String bucketName,String blockToken) throws Exception {
        if(blockToken==null || "".equals(blockToken.trim())){
            throw new OssException(ResultCode.BLOCK_TOKEN_NULL);
        }
        log.info("此次合并的blockToken:{}",blockToken);
        ChunkBo chunkBo = chunkRedisService.getChunkBo(bucketName,blockToken);
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
        //文件校验是否上传完所有分块
        if (chunkRedisService.isUploaded(bucketName,blockToken)) {
            log.info("所有分块上传完毕");
            log.info("preHandle:{}:{}",ip,port);
            UserSpecifiedAddressUtil.setAddress(new Address(ip,ossDataProvidePort, true));
            FilePrehandleBo filePrehandleBo = storageTempObjectService.preHandle(etag,blockToken,false,chunkBo.getSecret());
            if (filePrehandleBo==null) {
                //校验失败
                log.info("校验失败");
                chunkRedisService.removeChunk(bucketName,blockToken);
                UserSpecifiedAddressUtil.setAddress(new Address(ip,ossDataProvidePort, true));
                submitDelTask(new MqDelTmpBo(blockToken,ip,ossDataProvidePort));
                throw new OssException(ResultCode.CLIENT_ETAG_ERROR);
            } else {
                //校验成功
                log.info("校验成功");
                if(filePrehandleBo.getNewEtag()!=null){
                    etag = filePrehandleBo.getNewEtag();
                }
                log.info("最终的etag:{}", etag);
                ossObject.setExt(filePrehandleBo.getFileType());
                if(filePrehandleBo.getNewEtag()!=null){
                    etag = filePrehandleBo.getNewEtag();
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
                    log.info("文件不存在,需要进行落盘");
                    RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(OssApplicationConstant.NACOS_SERVER_ADDR,chunkBo.getGroupId());
                    String url = "http://" + ip + ":" + port + "/object/download_temp/" + blockToken;
                    LocationVo locationVo = new LocationVo(ip, port);
                    locationVo.setPath(url);
                    locationVo.setToken(blockToken);
                    if (RaftRpcRequest.save(leader.getCliClientService(), leader.getPeerId(), etag, locationVo)) {
                        System.out.println("所有节点完成同步!");
                        //删除缓存数据
                        submitDelTask(new MqDelTmpBo(blockToken,ip,ossDataProvidePort));
                    }else{
                        throw new OssException(ResultCode.CANT_SYNC);
                    }
                    //标记次数+1
                    norDuplicateRemovalService.save(etag, chunkBo.getGroupId());
                }
                Long bucketId = chunkBo.getBucketId();
                Bucket bucket = bucketMapper.selectById(bucketId);
                Long parentObjectId = chunkBo.getParentObjectId();
                // 删除redis相关信息
                chunkRedisService.removeChunk(bucketName,blockToken);
                //-----------持久化元数据-------------
                //插入文件夹
                Long parent = saveFolder(bucketId, chunkBo.getName(), parentObjectId);
                //真实
                ossObject.setBucketId(bucketId);String time = DateUtil.now();ossObject.setCreateTime(time);
                ossObject.setLastUpdateTime(time);ossObject.setSize(size);ossObject.setEtag(etag);
                ossObject.setName(chunkBo.getName());ossObject.setParent(parent);
                if(chunkBo.getObjectAcl()!=null){
                    ossObject.setObjectAcl(ACLEnum.getEnum(chunkBo.getObjectAcl()).getCode());
                }else{
                    ossObject.setObjectAcl(ACLEnum.DEFAULT.getCode());
                }
                ossObject.setStorageLevel(StorageTypeEnum.STANDARD.getCode());
                ossObject.setSecret(chunkBo.getSecret());
                ossObject.setIsFolder(false);
                Long id = ossObjectMapper.selectObjectIdByIdAndName(bucketId, ObjectName);
                if(id!=null){
                    //存在则更新
                    ossObject.setId(id);
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
    public OssObjectVo getObjectInfo(String bucketName, String objectName) {
        OssObjectVo ossObjectVo = new OssObjectVo();
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        BeanUtils.copyProperties(ossObject, ossObjectVo);
        return ossObjectVo;
    }



    @Override
    public Boolean deleteObjects(String bucketName) throws Exception {
        Long bucketId = bucketMapper.selectBucketIdByName(bucketName);
        List<OssObject> objects = ossObjectMapper.selectList(MPUtil.queryWrapperEq("bucket_id", bucketId));
        int count = ossObjectMapper.delete(MPUtil.queryWrapperEq("bucket_id", bucketId));
        for (OssObject ossObject : objects) {
            //删除对象
            deleteObject(bucketName, ossObject.getName());
            //删除对象标签
            objectTagObjectMapper.deleteTagByObjectId(ossObject.getId());
            objectTagObjectMapper.delete(MPUtil.queryWrapperEq("object_id",ossObject.getId()));
        }
        return true;
    }


    @Override
    public Boolean putFolder(String bucketName, String objectName, Long parentObjectId){
        Long id = bucketMapper.selectBucketIdByName(bucketName);
        if(id!=null){
            saveFolder(id,objectName,parentObjectId);
            return true;
        }else{
            throw new OssException(ResultCode.BUCKET_IS_DEFECT);
        }
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
        rPage.setTotalCountAndTotalPage(ossObjectMapper.selectObjectListLength(bucketName, key,parentObjectId,type));
        return rPage;
    }

    @Override
    public Boolean freeze(String bucketName, String objectName) throws Exception {
        //归档前必须处于正常状态
        Integer state = objectStateRedisService.getState(bucketName, objectName);
        if(!state.equals(ObjectStateConstant.NOR)){
            throw new OssException(ResultCode.CANT_SET_STATE);
        }
        //设置为归档中
        objectStateRedisService.setState(bucketName, objectName, ObjectStateConstant.FREEZING);
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        String etag = ossObject.getEtag();
        //现在需要进行归档处理,调用oss-cold-data服务将数据从oss-data下载并压缩保存
        Message msg = new Message(MessageQueueConstant.TOPIC_FREEZE,
                JSONObject.toJSONString(new ColdMqMessage(ossObject.getId(),etag)).getBytes(StandardCharsets.UTF_8));
        InitApplication.producer.send(msg);
        return true;
    }

    @Override
    public Boolean unfreeze(String bucketName, String objectName)throws Exception {
        //解冻前必须处于已经归档状态:
        Integer state = objectStateRedisService.getState(bucketName, objectName);
        if(!state.equals(ObjectStateConstant.FREEZE)){
            throw new OssException(ResultCode.CANT_SET_STATE);
        }
        objectStateRedisService.setState(bucketName, objectName, ObjectStateConstant.UNFREEZING);
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        String etag = ossObject.getEtag();
        //现在需要进行解冻处理,调用oss-data服务将数据从oss-cold-data下载并解压缩保存
        Message msg = new Message(MessageQueueConstant.TOPIC_UNFREEZE,
                JSONObject.toJSONString(new ColdMqMessage(ossObject.getId(),etag)).getBytes(StandardCharsets.UTF_8));
        InitApplication.producer.send(msg);
        return true;
    }



}

