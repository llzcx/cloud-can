package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.pojo.bo.BlockTokenBo;
import ccw.serviceinnovation.oss.pojo.dto.BatchDeletionObjectDto;
import ccw.serviceinnovation.oss.pojo.vo.*;
import com.alipay.sofa.jraft.error.RemotingException;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author 陈翔
 */
public interface IObjectService extends IService<OssObject> {

    Boolean deleteObject(String bucketName,String objectName) throws Exception;
    Boolean updateObjectName(String bucketName, String objectName,String newName);
    Boolean updateObjectAcl(String bucketName, String objectName,Integer objectAcl);
    Boolean upload(String bucketName, String objectName, String etag, MultipartFile file, Long  parentObjectId, Integer objectAcl) throws Exception;
    Boolean append(MultipartFile file, Integer chunk, String eventId, String bucketName) throws Exception;
    Boolean merge(String bucketName, String eventId) throws Exception;
    BlockTokenBo createUploadEvent(String etag, String bucketName, String objectName, Long parentObjectId,
                                   Integer objectAcl, Integer chunks, Long size) throws RemotingException, InterruptedException;
    OssObjectVo getObjectInfo(String bucketName, String objectName);
    void download(String bucketName,String objectName, HttpServletResponse response) throws IOException, RemotingException, InterruptedException;
    Boolean deleteObjects(String bucketName) throws Exception;
    Boolean putFolder(String bucketName, String objectName,Long parentObjectId);
    RPage<ObjectVo> listObjects(String bucketName, Integer pageNum, Integer size, String key,Long parentObjectId,Boolean isImages);
    Boolean freeze(String bucketName, String objectName) throws Exception;
    Boolean unfreeze(String bucketName, String objectName) throws Exception;
    ObjectStateVo getState(String bucketName,String objectName);
    Boolean backup(String sourceBucketName,String targetBucketName,String objectName,String newObjectName);
    Boolean backupRecovery(String bucketName,String objectName);

    Boolean batchDeletion(String bucketName, BatchDeletionObjectDto batchDeletionObjectDto) throws Exception;
    List<BackupObjectVo> listBackupObjects(String bucketName, String objectName);
}
