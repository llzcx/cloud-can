package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.BucketTag;
import ccw.serviceinnovation.common.entity.ObjectTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 杨世博
 */
@Mapper
@Repository
public interface BucketTagMapper extends BaseMapper<BucketTag> {

    /**
     * 获取Bucket标签
     * @param bucketId
     * @return
     */
    List<BucketTag> getBucketTag(Long bucketId);

    /**
     * 插入bucket标签
     * @param bucketTag
     * @return
     */
    Long insertTag(BucketTag bucketTag);
}
