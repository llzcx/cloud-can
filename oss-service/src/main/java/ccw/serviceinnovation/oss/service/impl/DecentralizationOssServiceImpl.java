package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.constant.ACLEnum;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.object.ObjectUtil;
import ccw.serviceinnovation.hash.directcalculator.EtagDirectCalculator;
import ccw.serviceinnovation.loadbalance.OssGroup;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.manager.authority.AuthContext;
import ccw.serviceinnovation.oss.manager.group.FindNodeHandler;
import ccw.serviceinnovation.oss.manager.redis.ChunkRedisService;
import ccw.serviceinnovation.oss.mapper.OssObjectMapper;
import ccw.serviceinnovation.oss.pojo.bo.BlockTokenBo;
import ccw.serviceinnovation.oss.pojo.bo.ChunkBo;
import ccw.serviceinnovation.oss.pojo.dto.BatchDeletionObjectDto;
import ccw.serviceinnovation.oss.pojo.vo.*;
import ccw.serviceinnovation.oss.service.IObjectService;
import ccw.serviceinnvation.nodeclient.RaftClient;
import cn.hutool.core.date.DateUtil;
import com.alipay.sofa.jraft.error.RemotingException;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import service.raft.request.*;
import service.raft.response.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static ccw.serviceinnovation.oss.constant.ObjectConstant.CHUNK_SIZE;

/**
 * 去中心化实现
 */
@Service
@Slf4j
public class DecentralizationOssServiceImpl extends ServiceImpl<OssObjectMapper, OssObject> implements IObjectService {


    @Autowired
    OssObjectMapper ossObjectMapper;

    @Autowired
    RaftClient client;

    @Autowired
    FindNodeHandler findNodeHandler;


    @Autowired
    EtagDirectCalculator etagDirectCalculator;

    @Autowired
    ChunkRedisService chunkRedisService;

    @Override
    public Boolean deleteObject(String bucketName, String objectName) throws Exception {
        OssObject ossObject = AuthContext.context().get().getOssObject();
        String etag = ossObject.getEtag();
        OssGroup ossGroup = findNodeHandler.find(etag);
        String groupName = ossGroup.getGroupName();
        client.sync(groupName, new DelRequest(etag), ResultCode.DELETE_ERROR);
        ossObjectMapper.deleteById(ossObject.getId());
        return true;
    }

    @Override
    public Boolean updateObjectName(String bucketName, String objectName, String newName) {

        return null;
    }

    @Override
    public Boolean updateObjectAcl(String bucketName, String objectName, Integer objectAcl) {

        return null;
    }

    @Override
    public Boolean upload(String bucketName, String objectName, String etag, MultipartFile file, Long parentObjectId, Integer objectAcl) throws Exception {
        Bucket bucket = AuthContext.context().get().getBucket();
        //TODO 检查
        if (file.getSize() > 5 * (1 << 20)) throw new OssException(ResultCode.FILE_IS_BIG);
        byte[] data = file.getBytes();
        //TODO PUT逻辑
        OssObject ossObject = ossObjectMapper.selectObjectByName(bucketName, objectName);
        if (ossObject != null) {
            if (ossObject.getEtag().equals(etag)) {
                return true;
            } else {
                //先执行删除
                OssGroup preGroup = findNodeHandler.find(etag);
                client.sync(preGroup.getGroupName(), new DelRequest(etag), ResultCode.DELETE_ERROR);
            }
        }
        //TODO 校验
        String realEtag = etagDirectCalculator.get(data);
        if (!Objects.equals(realEtag, etag)) throw new OssException(ResultCode.FILE_CHECK_ERROR);
        //TODO 定位
        OssGroup ossGroup = findNodeHandler.find(etag);
        UploadRequest uploadRequest = new UploadRequest(data, etag);
        //TODO 数据保存
        UploadResponse uploadResponse = (UploadResponse) client.sync(ossGroup.getGroupName(), uploadRequest, ResultCode.UPLOAD_ERROR);
        //TODO 元数据保存
        saveObject(bucket, objectName, parentObjectId, etag, null, (long) data.length, objectAcl);
        return true;
    }

    @Override
    public Boolean append(MultipartFile file, Integer chunk, String eventId, String bucketName) throws Exception {
        ChunkBo chunkBo = chunkRedisService.getChunkBo(bucketName, eventId);
        //TODO 元数据计算和校验
        if (chunkBo == null) throw new OssException(ResultCode.EVENT_NULL);
        byte[] data = file.getBytes();
        String groupName = chunkBo.getGroupId();
        //TODO 检验
        //总分片数
        int chunks = (int) Math.ceil((double) chunkBo.getSize() / CHUNK_SIZE);
        //总大小
        long size = chunkBo.getSize();
        //当前分片大小
        long currentSize = data.length;
        //最后一个分片大小
        long lastSize = size - (chunks - 1) * CHUNK_SIZE;
        if (chunk != chunks && currentSize != CHUNK_SIZE || chunk == chunks && currentSize != lastSize)
            throw new OssException(ResultCode.FRAGMENT_SIZE_ERROR);
        if (chunk > chunks) throw new OssException(ResultCode.OFFSET_LIMIT);
        Long off = CHUNK_SIZE * chunk;
        //TODO 保存到数据服务
        WriteFragmentRequest writeFragmentRequest = new WriteFragmentRequest(eventId, data, off, chunk);
        WriteFragmentResponse writeFragmentResponse = (WriteFragmentResponse) client.sync(groupName, writeFragmentRequest, ResultCode.SERVER_EXCEPTION);
        return true;
    }

    @Override
    public Boolean merge(String bucketName, String eventId) throws Exception {
        if (eventId == null || "".equals(eventId.trim())) throw new OssException(ResultCode.EVENT_ID_NULL);
        ChunkBo chunkBo = chunkRedisService.getChunkBo(bucketName, eventId);
        String groupName = chunkBo.getGroupId();
        String etag = chunkBo.getEtag();
        WriterMergeRequest writerMergeRequest = new WriterMergeRequest(eventId, etag);
        WriterMergeResponse writerMergeResponse = (WriterMergeResponse) client.sync(groupName, writerMergeRequest, ResultCode.SERVER_EXCEPTION);
        if (!writerMergeResponse.getMergeSuccess()) throw new OssException(writerMergeResponse.getResultCode());
        WriteDelEventRequest writeDelEventRequest = new WriteDelEventRequest(eventId);
        WriteDelEventResponse writeDelEventResponse = (WriteDelEventResponse) client.sync(groupName, writeDelEventRequest, ResultCode.SERVER_EXCEPTION);
        //TODO 保存元数据
        saveObject(AuthContext.context().get().getBucket(), chunkBo.getName(),
                chunkBo.getParentObjectId(), etag, null, chunkBo.getSize(), chunkBo.getObjectAcl());
        return true;
    }

    @Override
    public BlockTokenBo createUploadEvent(String etag, String bucketName, String objectName, Long parentObjectId, Integer objectAcl, Integer chunks, Long size) throws RemotingException, InterruptedException {
        Bucket bucket = AuthContext.context().get().getBucket();
        BlockTokenBo blockTokenBo = new BlockTokenBo();
        //TODO 文件上传事件
        String eventId = UUID.randomUUID().toString().replace("-", "");
        blockTokenBo.setEventId(eventId);
        //TODO 数据层处理
        OssGroup ossGroup = findNodeHandler.find(etag);
        String groupName = ossGroup.getGroupName();
        int chunks2 = (int) Math.ceil((double) size / CHUNK_SIZE);
        if (chunks2 != chunks) throw new OssException(ResultCode.FILE_CHECK_ERROR);
        WriteEventRequest writeEventRequest = new WriteEventRequest(etag, eventId, size, chunks2);
        WriteEventResponse writeEventResponse = (WriteEventResponse) client.sync(groupName, writeEventRequest, ResultCode.SERVER_EXCEPTION);
        //TODO 文件秒传逻辑
        if (writeEventResponse.getSecondTransmission()) {
            blockTokenBo.setExist(true);
            return blockTokenBo;
        } else {
            blockTokenBo.setExist(false);
        }
        //TODO 保存事件元数据
        if (objectAcl == null) objectAcl = ACLEnum.DEFAULT.getCode();
        chunkRedisService.saveBlockToken(bucketName, eventId, etag, bucket.getUserId(),
                bucket.getId(), size, parentObjectId, objectAcl, objectName, groupName);
        return blockTokenBo;
    }

    @Override
    public OssObjectVo getObjectInfo(String bucketName, String objectName) {

        return null;
    }

    @Override
    public void download(String bucketName, String objectName, HttpServletResponse response) throws IOException, RemotingException, InterruptedException {
        OssObject ossObject = AuthContext.context().get().getOssObject();
        String etag = ossObject.getEtag();
        ServletOutputStream outputStream = response.getOutputStream();
        OssGroup ossGroup = findNodeHandler.find(etag);
        try {
            client.transferTo(outputStream, ossGroup.getGroupName(), etag, 0, -1);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public Boolean deleteObjects(String bucketName) throws Exception {
        return null;
    }

    @Override
    public Boolean putFolder(String bucketName, String objectName, Long parentObjectId) {
        return null;
    }

    @Override
    public RPage<ObjectVo> listObjects(String bucketName, Integer pageNum, Integer size, String key, Long parentObjectId, Boolean isImages) {
        return null;
    }

    @Override
    public Boolean freeze(String bucketName, String objectName) throws Exception {
        return null;
    }

    @Override
    public Boolean unfreeze(String bucketName, String objectName) throws Exception {
        return null;
    }

    @Override
    public ObjectStateVo getState(String bucketName, String objectName) {
        return null;
    }

    @Override
    public Boolean backup(String sourceBucketName, String targetBucketName, String objectName, String newObjectName) {
        return null;
    }

    @Override
    public Boolean backupRecovery(String bucketName, String objectName) {
        return null;
    }

    @Override
    public Boolean batchDeletion(String bucketName, BatchDeletionObjectDto batchDeletionObjectDto) throws Exception {
        return null;
    }

    @Override
    public List<BackupObjectVo> listBackupObjects(String bucketName, String objectName) {
        return null;
    }

    @Override
    public Boolean deleteAll(String bucketName) throws Exception {
        Bucket bucket = AuthContext.context().get().getBucket();
        List<OssObject> objectList = ossObjectMapper.selectByMap(MPUtil.getMap("bucket_id", bucket.getId()));
        for (OssObject ossObject : objectList) {
            String etag = ossObject.getEtag();
            OssGroup ossGroup = client.find(etag);
            DelRequest delRequest = new DelRequest(etag);
            DelResponse delResponse = (DelResponse) client.sync(ossGroup.getGroupName(), delRequest, ResultCode.DELETE_ERROR);
            if (delResponse.getDelSuccess()) ossObjectMapper.deleteById(ossObject.getId());
        }
        return true;
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
            ossObjectMapper.updateById(ossObject);
        } else {
            //不存在则插入
            ossObjectMapper.insert(ossObject);
        }
    }
}
