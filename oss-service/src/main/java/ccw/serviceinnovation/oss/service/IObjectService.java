package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.pojo.bo.BlockTokenBo;
import ccw.serviceinnovation.oss.pojo.bo.GetObjectBo;
import ccw.serviceinnovation.oss.pojo.vo.ObjectStateVo;
import ccw.serviceinnovation.oss.pojo.vo.ObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.OssObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * @author 陈翔
 */
public interface IObjectService extends IService<OssObject> {

    /**
     * 从桶里删除一个用户的对象
     * @param objectName
     * @param bucketName
     * @return 返回是否删除成功
     * @throws IOException
     */
    Boolean deleteObject(String bucketName,String objectName) throws Exception;


    /**
     * 用户从桶里添加一个对象[小文件上传]
     * @param bucketName
     * @param objectName
     * @param etag
     * @param file
     * @return
     * @throws IOException
     */
    Boolean addSmallObject(String bucketName, String objectName, String etag, MultipartFile file,Long  parentObjectId,Integer objectAcl) throws Exception;


    /**
     * 用户从桶里添加一个对象的分块
     * @param file 文件
     * @param chunk 第几块
     * @param blockToken 凭证
     * @return
     * @throws Exception
     */
    Boolean addObjectChunk(MultipartFile file, Integer chunk, String blockToken) throws Exception;

    /**
     * 合并分块
     * @param blockToken
     * @return
     * @throws Exception
     */
    Boolean mergeObjectChunk(String blockToken) throws Exception;
    /**
     *
     * @param etag 对象MD5值
     * @param bucketName 桶名
     * @param objectName 对象名
     * @param parentObjectId
     * @param chunks 总块数
     * @param size 大小
     * @return
     */
    BlockTokenBo getBlockToken(String etag, String bucketName, String objectName, Long parentObjectId,
                               Integer objectAcl, Integer chunks, Long size);

    /**
     * 用户从一个桶当中获取一个oss对象
     * @param bucketName 桶名字
     * @param objectName 对象名
     * @return 返回查询到的数据
     * @throws IOException
     */
    OssObjectVo getObjectInfo(String bucketName, String objectName);

    /**
     * 删除一个桶当中所有对象
     * @param bucketName
     * @return
     */
    Boolean deleteObjects(String bucketName) throws Exception;

    /**
     * 添加一个文件夹
     * @param bucketName
     * @param objectName
     * @return
     */
    Boolean putFolder(String bucketName, String objectName,Long parentObjectId);

    /**
     * 列出对象列表
     * @param bucketName
     * @param pageNum
     * @param size
     * @param key
     * @return
     */
    RPage<ObjectVo> listObjects(String bucketName, Integer pageNum, Integer size, String key,Long parentObjectId,Boolean isImages);


    /**
     * 归档一个对象
     * @param bucketName
     * @param objectName
     * @return
     */
    Boolean freeze(String bucketName, String objectName) throws Exception;


    /**
     * 解冻一个对象
     * @param bucketName
     * @param objectName
     * @return
     */
    Boolean unfreeze(String bucketName, String objectName) throws Exception;


    /**
     * 获取当前对象的状态
     * @param bucketName
     * @param objectName
     * @return
     */
    ObjectStateVo getState(String bucketName,String objectName);


    /**
     * 将bucketName的对象objectName 备份到 bucketNameBackup
     * @param sourceBucketName
     * @param targetBucketName
     * @param objectName
     * @param newObjectName
     * @return
     */
    Boolean backup(String sourceBucketName,String targetBucketName,String objectName,String newObjectName);


    /**
     * 复原数据
     * @param bucketName 桶名字
     * @param objectName 对象名字
     * @return
     */
    Boolean backupRecovery(String bucketName,String objectName);
}
