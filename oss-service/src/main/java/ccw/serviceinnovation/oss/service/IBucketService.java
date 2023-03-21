package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.oss.pojo.dto.AddBucketDto;
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
     * @return 返回桶列表
     * @throws IOException
     */
    List<Bucket> getBucketList(Long userId) throws IOException;


    /**
     * 获取桶信息
     * @param bucketName
     * @return
     */
    Bucket getBucketInfo(String bucketName);

}
