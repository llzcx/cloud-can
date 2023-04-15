package ccw.serviceinnovation.ossgateway.mapper;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.OssObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 陈翔
 */
@Mapper
public interface OssObjectMapper extends BaseMapper<OssObject> {
    /**
     * 根据桶名字查找对象id
     * @param bucketName
     * @param objectName
     * @return
     */
    OssObject selectObjectIdByName(@Param("bucketName")String bucketName, @Param("objectName")String objectName);


    /**
     * 查找一个对象的存储状态
     * @param bucketName
     * @param objectName
     * @return
     */
    Integer selectObjectStorageLevel(@Param("bucketName")String bucketName,@Param("objectName")String objectName);

    Integer selectBucketByName(@Param("bucketName")String bucketName);

}
