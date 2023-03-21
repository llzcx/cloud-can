package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.Bucket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 陈翔
 */
@Mapper
public interface BucketMapper extends BaseMapper<Bucket> {

    /**
     * 根据桶名字查找桶id
     * @param bucketName
     * @return
     */
    Long selectBucketIdByName(@Param("bucketName")String bucketName);

    /**
     * 根据桶名字查找桶
     * @param bucketName
     * @return
     */
    Bucket selectBucketByName(@Param("bucketName")String bucketName);


    /**
     * 根据bucketId获取bucketName
     * @param bucketId
     * @return
     */
    String getBucketName(@Param("bucketId")Long bucketId);
}
