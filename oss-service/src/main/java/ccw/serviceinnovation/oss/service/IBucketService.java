package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.oss.pojo.dto.AddBucketDto;
import ccw.serviceinnovation.oss.pojo.vo.FileTypeVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * @author 陈翔
 */
public interface IBucketService extends IService<Bucket> {
    /**
     * 用户创建一个桶
     * @param addBucketDto 桶参数传输对象
     * @param userId 用户id
     * @return 返回是否
     * @throws IOException
     */
    Bucket createBucket(AddBucketDto addBucketDto, Long userId) throws IOException;


    /**
     * 删除一个用户的桶
     * @param bucketName ID
     * @return 返回是否删除2成功
     * @throws Exception
     */
    Boolean deleteBucket(String bucketName) throws Exception;

    /**
     * 获取用户的桶列表
     * @param userId 用户ID
     * @param pageNum
     * @param size
     * @param key
     * @return 返回桶列表
     * @throws IOException
     */
    RPage<Bucket> getBucketList(Long userId, Integer pageNum, Integer size, String key) throws IOException;


    /**
     * 获取桶信息
     * @param bucketName
     * @return
     */
    Bucket getBucketInfo(String bucketName);

    /**
     * 更新存储类型
     * @param bucketName
     * @param storageLevel
     * @return
     */
    Boolean updateStorageLevel(String bucketName,Integer storageLevel);


    /**
     * 更新bucket的权限
     * @param bucketName
     * @param bucketAcl
     * @return
     */
    Boolean updateBucketAcl(String bucketName,Integer bucketAcl);


    /**
     * 更新加密方式
     * @param bucketName
     * @param secret
     * @return
     */
    Boolean updateSecret(String bucketName,Integer secret);


    /**
     * 获取bucket中存在的文件类型
     * @param bucketName
     * @returnu
     */
    List<FileTypeVo> getBucketFileType(String bucketName);

    /**
     * 获取该用户所有bucket内的文件类型
     * @param userId
     * @return
     */
    List<FileTypeVo> getUserBucketFileType(Long userId);

    /**
     * 桶重命名
     * @param bucketName
     * @param newBucketName
     * @return
     */
    Boolean reName(String bucketName,String newBucketName);
}
