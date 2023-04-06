package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.BucketTagBucket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;


/**
 * @author Joy Yang
 */
@Mapper
public interface BucketTagBucketMapper extends BaseMapper<BucketTagBucket> {

    /**
     * 根据BucketId删除对应的Tag
     * @param bucketId
     * @return
     */
    Long deleteTagByBucketId(Long bucketId);

    /**
     * 插入标签
     * @param tagId
     * @param bucketId
     */
    void insertTag(Long tagId, Long bucketId);
}
