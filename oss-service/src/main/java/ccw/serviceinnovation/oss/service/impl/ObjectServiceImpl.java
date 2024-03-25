package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.constant.ACLEnum;
import ccw.serviceinnovation.common.constant.FileTypeConstant;
import ccw.serviceinnovation.common.entity.*;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.NodeObjectKeyUtil;
import ccw.serviceinnovation.common.util.object.ObjectUtil;
import ccw.serviceinnovation.hash.checksum.Crc32EtagHandlerAdapter;
import ccw.serviceinnovation.hash.checksum.EtagHandler;
import ccw.serviceinnovation.loadbalance.OssGroup;
import ccw.serviceinnovation.oss.common.util.ControllerUtils;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.manager.authority.AuthContext;
import ccw.serviceinnovation.oss.manager.group.FindNodeHandler;
import ccw.serviceinnovation.oss.manager.redis.ChunkRedisService;
import ccw.serviceinnovation.oss.manager.redis.NorDuplicateRemovalService;
import ccw.serviceinnovation.oss.mapper.*;
import ccw.serviceinnovation.oss.pojo.bo.BlockTokenBo;
import ccw.serviceinnovation.oss.pojo.bo.ChunkBo;
import ccw.serviceinnovation.oss.pojo.dto.BatchDeletionObjectDto;
import ccw.serviceinnovation.oss.pojo.vo.*;
import ccw.serviceinnovation.oss.service.IObjectService;
import ccw.serviceinnvation.nodeclient.RaftClient;
import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.sofa.jraft.error.RemotingException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import service.raft.request.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.Checksum;

import static ccw.serviceinnovation.oss.constant.ObjectConstant.CHUNK_SIZE;


/**
 * @author 陈翔
 */
//@Service
@Slf4j
//@Transactional(rollbackFor = {Exception.class, RuntimeException.class})
public class ObjectServiceImpl extends ServiceImpl<OssObjectMapper, OssObject> implements IObjectService {

    @Autowired
    private OssObjectMapper ossObjectMapper;

    @Autowired
    private BucketMapper bucketMapper;

    @Autowired
    private ChunkRedisService chunkRedisService;


    @Autowired
    private NorDuplicateRemovalService norDuplicateRemovalService;


    @Autowired
    BackupMapper backupMapper;

    @Autowired
    ObjectTagMapper objectTagMapper;

    @Autowired
    ObjectTagObjectMapper objectTagObjectMapper;

    @Autowired
    FindNodeHandler findNodeHandler;

    @Autowired
    RaftClient raftClient;

    @Autowired
    EtagHandler etagHandler;


    @Override
    public ObjectStateVo getState(String bucketName, String objectName) {
        throw new OssException(ResultCode.NO_IMPL);
    }

    @Override
    public Boolean backup(String sourceBucketName, String targetBucketName, String objectName, String newObjectName) {
        String backupObjectName;
        if (newObjectName != null) {
            backupObjectName = newObjectName;
        } else {
            //新名字格式为:backupObject-sourceBucket-objectName
            backupObjectName = "backupObject-" + sourceBucketName + "-" + objectName;
        }
        //源对象数据
        OssObject sourceOssObject = ossObjectMapper.selectObjectByName(sourceBucketName, objectName);
        //目标桶Id
        Long id = bucketMapper.selectBucketIdByName(targetBucketName);
        //源对象的
        String SourceEtag = sourceOssObject.getEtag();
        //引用次数+1
        String group = norDuplicateRemovalService.getGroup(SourceEtag);
        if (group == null) {
            throw new OssException(ResultCode.SYSTEM_ERROR_DATA_NULL);
        }
        norDuplicateRemovalService.save(SourceEtag, group);
        //插入元数据
        OssObject ossObject = new OssObject();
        ossObject.setIsBackup(true);
        //设置备份名字
        ossObject.setName(backupObjectName);
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
        OssObject ossObject1 = ossObjectMapper.selectObjectByName(targetBucketName, backupObjectName);
        if (ossObject1 == null) {
            ossObjectMapper.insert(ossObject);
        } else {
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
    public Boolean backupRecovery(String bucketName, String objectName) {
        log.info("{}/{}", bucketName, objectName);
        //只需要一个备份对象即可
        Backup backups = backupMapper.selectBackupByTarget(bucketName, objectName);
        log.info("{}", JSONObject.toJSONString(backups));
        if (backups == null) {
            throw new OssException(ResultCode.BACKUP_DATA_NULL);
        }
        Long targetObjectId = backups.getTargetObjectId();
        //拿到源对象
        OssObject sourceOssObject = ossObjectMapper.selectById(backups.getSourceObjectId());
        String sourceGroup = norDuplicateRemovalService.getGroup(sourceOssObject.getEtag());
        //拿到目标对象
        OssObject targetOssObject = ossObjectMapper.selectById(targetObjectId);
        String targetGroup = norDuplicateRemovalService.getGroup(targetOssObject.getEtag());

        if (sourceGroup == null || targetGroup == null) {
            throw new OssException(ResultCode.SYSTEM_ERROR_DATA_NULL);
        }
        //改变原来的数据的引用 target+1 source-1
        norDuplicateRemovalService.save(targetOssObject.getEtag(), targetGroup);
        norDuplicateRemovalService.del(sourceOssObject.getEtag());

        //更新源对象
        sourceOssObject.setEtag(targetOssObject.getEtag());
        ossObjectMapper.updateById(sourceOssObject);
        return true;
    }

    @Override
    public Boolean batchDeletion(String bucketName, BatchDeletionObjectDto batchDeletionObjectDto) throws Exception {
        String objectNameListJson = batchDeletionObjectDto.getObjectNameListJson();
        JSONArray objects = JSONObject.parseArray(objectNameListJson);
        for (Object object : objects) {
            if (!deleteObject(bucketName, (String) object)) {
                throw new OssException(ResultCode.FILE_DELETE_ERROR);
            }
        }
        return true;
    }

    @Override
    public List<BackupObjectVo> listBackupObjects(String bucketName, String objectName) {
        List<Backup> backups = backupMapper.selectBackup(bucketName, objectName);
        List<BackupObjectVo> list = new ArrayList<>();
        for (Backup backup : backups) {
            Long targetObjectId = backup.getTargetObjectId();
            OssObject ossObject = ossObjectMapper.selectById(targetObjectId);
            BackupObjectVo backupObjectVo = new BackupObjectVo();
            backupObjectVo.setId(ossObject.getId());
            backupObjectVo.setObjectName(ossObject.getName());
            backupObjectVo.setCreateTime(backup.getCreateTime());
            backupObjectVo.setBucketName(bucketMapper.selectById(ossObject.getBucketId()).getName());
            list.add(backupObjectVo);
        }
        return list;
    }


    @Override
    public Boolean deleteObject(String bucketName, String objectName) throws Exception {
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        if (ossObject == null) return true;
        if (!ossObject.getIsFolder()) {
            String etag = ossObject.getEtag();
            NodeObjectKeyUtil.getObjectKey(etag);
            String group = norDuplicateRemovalService.getGroup(NodeObjectKeyUtil.getObjectKey(etag));
            raftClient.sync(group, new DelRequest(NodeObjectKeyUtil.getObjectKey(etag)), ResultCode.SERVER_EXCEPTION);
            norDuplicateRemovalService.del(NodeObjectKeyUtil.getObjectKey(etag));
        }
        //删除元数据
        ossObjectMapper.deleteById(ossObject.getId());
        //删除对象标签
        List<ObjectTagObject> objectTagObjects = objectTagObjectMapper.selectList(MPUtil.queryWrapperEq("object_id", ossObject.getId()));
        objectTagObjectMapper.delete(MPUtil.queryWrapperEq("object_id", ossObject.getId()));
        for (ObjectTagObject objectTagObject : objectTagObjects) {
            objectTagMapper.delete(MPUtil.queryWrapperEq("id", objectTagObject.getTagId()));
        }
        return true;
    }

    @Override
    public Boolean updateObjectName(String bucketName, String objectName, String newName) {
        //查询这个文件名newName是否已经存在
        Long id = ossObjectMapper.selectObjectIdByName(bucketName, newName);
        if (id == null) {
            //不存在
            OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
            ossObject.setName(newName);
            ossObjectMapper.updateById(ossObject);
            return true;
        } else {
            //存在
            throw new OssException(ResultCode.NAME_IS_EXIST);
        }
    }

    @Override
    public Boolean updateObjectAcl(String bucketName, String objectName, Integer objectAcl) {
        //查询这个文件名是否已经存在
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        if (ossObject == null) {
            //不存在
            throw new OssException(ResultCode.OBJECT_IS_DEFECT);
        } else {
            //存在
            ACLEnum anEnum = ACLEnum.getEnum(objectAcl);
            if (anEnum == null) {
                throw new OssException(ResultCode.UNDEFINED);
            }
            ossObject.setObjectAcl(anEnum.getCode());
            ossObjectMapper.updateById(ossObject);
            return true;
        }
    }

    public void saveObject(Bucket bucket, String objectName, Long parentObjectId, String etag, Integer ext, Long size, Integer objectAcl) {
        //-----------持久化元数据-------------
        //插入文件夹
        Long parent = saveFolder(bucket.getId(), objectName, parentObjectId);
        OssObject ossObject = new OssObject();
        ossObject.setName(objectName);
        ossObject.setParent(parent);
        String time = DateUtil.now();
        ossObject.setLastUpdateTime(time);
        ossObject.setCreateTime(time);
        ossObject.setBucketId(bucket.getId());
        ossObject.setEtag(etag);
        ossObject.setExt(ext);
        ossObject.setIsFolder(false);
        ossObject.setSize(size);
        ossObject.setIsBackup(false);
        if (objectAcl != null) {
            ossObject.setObjectAcl(ACLEnum.getEnum(objectAcl).getCode());
        } else {
            ossObject.setObjectAcl(ACLEnum.PRIVATE.getCode());
        }
        OssObject os = ossObjectMapper.selectObjectByName(bucket.getName(), objectName);
        if (os != null) {
            //存在则更新
            ossObject.setId(os.getId());
            norDuplicateRemovalService.del(ossObject.getEtag());
            ossObjectMapper.updateById(ossObject);
        } else {
            //不存在则插入
            ossObjectMapper.insert(ossObject);
        }
    }


    @Override
    public Boolean upload(String bucketName, String objectName, String etag, MultipartFile file,
                          Long parentObjectId, Integer objectAcl) throws Exception {
        Bucket bucket = AuthContext.context().get().getBucket();
        //TODO 检查
        if (file.getSize() > 5 * (1 << 20)) throw new OssException(ResultCode.FILE_IS_BIG);
        //TODO 对象去重
        String oldGroupName = norDuplicateRemovalService.getGroup(NodeObjectKeyUtil.getObjectKey(etag));
        if (oldGroupName != null) {
            log.info("{} exist!", etag);
            saveObject(bucket, objectName, parentObjectId, etag, null, file.getSize(), ACLEnum.PRIVATE.getCode());
            return true;
        }
        //TODO 保存二进制数据
        byte[] bytes = file.getBytes();
        //TODO 对象校验
        Checksum checksum = etagHandler.deserialize("0");
        String calculateEtag = etagHandler.serialize(checksum);
        System.out.println("calculateEtag:" + calculateEtag);
        if (!calculateEtag.equals(etag)) {
            throw new OssException(ResultCode.FILE_CHECK_ERROR);
        }
        //TODO 寻找存储的节点
        OssGroup ossGroup = findNodeHandler.find(NodeObjectKeyUtil.getObjectKey(etag));
        String newGroupName = ossGroup.getGroupName();
        UploadRequest uploadRequest = new UploadRequest(bytes, NodeObjectKeyUtil.getObjectKey(calculateEtag));
        raftClient.sync(newGroupName, uploadRequest, ResultCode.UPLOAD_ERROR);
        //TODO 保存元数据
        saveObject(bucket, objectName, parentObjectId, etag, null, file.getSize(), ACLEnum.PRIVATE.getCode());
        norDuplicateRemovalService.save(NodeObjectKeyUtil.getObjectKey(etag), newGroupName);
        return true;
    }


    @Override
    public BlockTokenBo createUploadEvent(String etag, String bucketName, String objectName, Long parentObjectId,
                                          Integer objectAcl, Integer chunks, Long size) throws RemotingException, InterruptedException {
        Bucket bucket = AuthContext.context().get().getBucket();
        BlockTokenBo blockTokenBo = new BlockTokenBo();
        //TODO 文件秒传逻辑
        String group = norDuplicateRemovalService.getGroup(NodeObjectKeyUtil.getObjectKey(etag));
        if (group != null) {
            blockTokenBo.setExist(true);
            return blockTokenBo;
        } else {
            blockTokenBo.setExist(false);
        }
        // TODO 文件分片上传逻辑
        String eventId = UUID.randomUUID().toString().replace('-', '_');
        blockTokenBo.setEventId(eventId);
        //TODO 数据层处理
        OssGroup ossGroup = findNodeHandler.find(NodeObjectKeyUtil.getObjectKey(etag));
        String groupName = ossGroup.getGroupName();
        WriteEventRequest writeEventRequest = new WriteEventRequest(NodeObjectKeyUtil.getObjectKey(etag), eventId, size,null);
        raftClient.sync(groupName, writeEventRequest, ResultCode.SERVER_EXCEPTION);
        //TODO 保存事件元数据
        if (objectAcl == null) objectAcl = ACLEnum.DEFAULT.getCode();
        chunkRedisService.saveBlockToken(bucketName, eventId, NodeObjectKeyUtil.getObjectKey(etag), bucket.getUserId(),
                bucket.getId(), size, parentObjectId, objectAcl, objectName, groupName);
        return blockTokenBo;
    }

    @Override
    public Boolean append(MultipartFile file, Integer chunk, String eventId, String bucketName) throws Exception {
        ChunkBo chunkBo = chunkRedisService.getChunkBo(bucketName, eventId);
        //TODO 元数据计算和校验
        if (chunkBo == null) throw new OssException(ResultCode.EVENT_NULL);
        String groupName = chunkBo.getGroupId();
        byte[] bytes = file.getBytes();
        int chunks = (int) Math.ceil((double) chunkBo.getSize() / CHUNK_SIZE);
        if (chunk != chunks && bytes.length != CHUNK_SIZE) throw new OssException(ResultCode.FRAGMENT_SIZE_ERROR);
        Long off = CHUNK_SIZE * chunk;
        if (off > chunkBo.getSize()) throw new OssException(ResultCode.OFFSET_LIMIT);
        String checkSum = chunkRedisService.getCheckSum(eventId);
        Checksum deserialize = etagHandler.deserialize(checkSum);
        etagHandler.update(deserialize, bytes, 0, bytes.length);
        //TODO 保存到数据服务
        if (bytes.length != CHUNK_SIZE) throw new OssException(ResultCode.FRAGMENT_SIZE_ERROR);
        WriteFragmentRequest writeFragmentRequest = new WriteFragmentRequest(eventId, bytes, off,null);
        raftClient.sync(groupName, writeFragmentRequest, ResultCode.SERVER_EXCEPTION);
        //TODO 保存元数据
        chunkRedisService.saveChunkBit(eventId, chunk);
        chunkRedisService.saveCheckSum(eventId, etagHandler.serialize(deserialize));
        return true;
    }

    /**
     * 插入文件夹
     *
     * @param bucketId
     * @param ossObject
     */
    public void insertFolder(Long bucketId, OssObject ossObject) {
        Long id = ossObjectMapper.selectObjectIdByIdAndName(bucketId, ossObject.getName());
        if (id == null) ossObjectMapper.insert(ossObject);
    }

    public Long saveFolder(Long bucketId, String objectName, Long parentId) {
        if (bucketId == null) {
            throw new OssException(ResultCode.BUCKET_IS_DEFECT);
        }
        String[] arr = ObjectUtil.getAllFolder(objectName);
        if (arr != null) {
            StringBuilder prefixName = new StringBuilder();
            if (parentId != null) {
                OssObject ossObject2 = ossObjectMapper.selectById(parentId);
                if (ossObject2 == null || !ossObject2.getIsFolder()) {
                    throw new OssException(ResultCode.PARENT_ID_IS_INVALID);
                }
                prefixName.append(ossObject2.getName());
            }
            Long prefixId = parentId;
            for (String s : arr) {
                OssObject ossObject1 = new OssObject();
                ossObject1.setIsFolder(true);
                ossObject1.setParent(prefixId);
                ossObject1.setBucketId(bucketId);
                prefixName.append(s).append("/");
                ossObject1.setName(prefixName.toString());
                ossObject1.setBucketId(bucketId);
                //插入文件夹
                log.info("insert: {}", ossObject1.getName());
                insertFolder(bucketId, ossObject1);
                prefixId = ossObject1.getId();
            }
            return prefixId;
        }
        return null;
    }


    @Override
    public Boolean merge(String bucketName, String eventId) throws Exception {
        if (eventId == null || "".equals(eventId.trim())) throw new OssException(ResultCode.EVENT_ID_NULL);
        Bucket bucket = AuthContext.context().get().getBucket();
        ChunkBo chunkBo = chunkRedisService.getChunkBo(bucketName, eventId);
        Long size = chunkBo.getSize();
        String etag = chunkBo.getEtag();
        Long bucketId = chunkBo.getBucketId();
        String groupName = chunkBo.getGroupId();
        Long parentObjectId = chunkBo.getParentObjectId();
        Long userId = chunkBo.getUserId();
        Integer objectAcl = chunkBo.getObjectAcl();
        String objectName = chunkBo.getName();
        WriteDelEventRequest writeDelEventRequest = new WriteDelEventRequest(eventId);
        //TODO 分片数量检查
        int current = chunkRedisService.counter(bucketName, eventId);
        int chunks = (int) Math.ceil((double) size / CHUNK_SIZE);
        if (current != chunks) throw new OssException(ResultCode.CHUNK_NOT_UP_FINISH);
        //TODO 对象校验
        String calculateEtag = chunkRedisService.getCheckSum(etag);
        if (!Objects.equals(etag, calculateEtag)) {
            raftClient.sync(groupName, writeDelEventRequest, ResultCode.SERVER_EXCEPTION);
            throw new OssException(ResultCode.FILE_CHECK_ERROR);
        }
        //TODO 对象去重
        String oldGroupName = norDuplicateRemovalService.getGroup(NodeObjectKeyUtil.getObjectKey(etag));
        if (oldGroupName != null) {
            log.info("{} exist.", etag);
            saveObject(bucket, objectName, parentObjectId, etag, null, size, ACLEnum.PRIVATE.getCode());
            raftClient.sync(groupName, writeDelEventRequest, ResultCode.SERVER_EXCEPTION);
            return true;
        }
        //TODO 数据层合并
        WriterMergeRequest writerMergeRequest = new WriterMergeRequest(eventId, NodeObjectKeyUtil.getObjectKey(etag));
        raftClient.sync(groupName, writerMergeRequest, ResultCode.SERVER_EXCEPTION);
        //TODO 保存元数据
        saveObject(bucket, objectName, parentObjectId, NodeObjectKeyUtil.getObjectKey(etag), null, size, ACLEnum.PRIVATE.getCode());
        norDuplicateRemovalService.save(NodeObjectKeyUtil.getObjectKey(etag), groupName);
        return true;
    }


    @Override
    public OssObjectVo getObjectInfo(String bucketName, String objectName) {
        OssObjectVo ossObjectVo = new OssObjectVo();
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        BeanUtils.copyProperties(ossObject, ossObjectVo);
        return ossObjectVo;
    }

    @Override
    public void download(String bucketName, String objectName, HttpServletResponse response) throws IOException, RemotingException, InterruptedException {
        OssObject ossObject = AuthContext.context().get().getOssObject();
        String etag = ossObject.getEtag();
        ServletOutputStream outputStream = response.getOutputStream();
        String group = norDuplicateRemovalService.getGroup(etag);
        try {
            raftClient.transferTo(outputStream, group, NodeObjectKeyUtil.getObjectKey(etag), 0, -1);
        }catch (OssException e){
            e.printStackTrace();
            response.setStatus(HttpStatus.HTTP_NOT_FOUND);
            ControllerUtils.writeIfReturn(response,e.getResultCode(),false);
        }
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
            objectTagObjectMapper.delete(MPUtil.queryWrapperEq("object_id", ossObject.getId()));
        }
        return true;
    }


    @Override
    public Boolean putFolder(String bucketName, String objectName, Long parentObjectId) {
        Long id = bucketMapper.selectBucketIdByName(bucketName);
        if (id != null) {
            saveFolder(id, objectName, parentObjectId);
            return true;
        } else {
            throw new OssException(ResultCode.BUCKET_IS_DEFECT);
        }
    }

    @Override
    public RPage<ObjectVo> listObjects(String bucketName, Integer pageNum, Integer size, String key, Long parentObjectId, Boolean isImages) {
        Integer offset = null;
        if (pageNum != null) {
            offset = (pageNum - 1) * size;
        }
        Integer type = null;
        if (isImages != null) {
            type = FileTypeConstant.IMG;
        }
        List<ObjectVo> list = ossObjectMapper.selectObjectList(bucketName, offset, size, key, parentObjectId, type);
        RPage<ObjectVo> rPage = new RPage<>(pageNum, size, list);
        rPage.setTotalCountAndTotalPage(ossObjectMapper.selectObjectListLength(bucketName, key, parentObjectId, type));
        return rPage;
    }

    @Override
    public Boolean freeze(String bucketName, String objectName) throws Exception {
        throw new OssException(ResultCode.NO_IMPL);
    }

    @Override
    public Boolean unfreeze(String bucketName, String objectName) throws Exception {
        throw new OssException(ResultCode.NO_IMPL);
    }


}

