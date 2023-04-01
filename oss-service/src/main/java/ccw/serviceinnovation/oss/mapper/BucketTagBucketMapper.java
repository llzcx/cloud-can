package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.BucketTagBucket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;


/**
 * @author Joy Yang
 */
public interface BucketTagBucketMapper extends BaseMapper<BucketTagBucket> {

    /**
     * 根据BucketId删除对应的Tag
     * @param bucketId
     * @return
     */
    Long deleteTagByBucketId(Long bucketId);
}
