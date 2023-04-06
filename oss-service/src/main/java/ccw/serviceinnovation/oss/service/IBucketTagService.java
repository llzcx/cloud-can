package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.BucketTag;
import ccw.serviceinnovation.oss.pojo.dto.DeleteBucketTagDto;
import ccw.serviceinnovation.oss.pojo.dto.PutBucketTagDto;
import ccw.serviceinnovation.oss.pojo.dto.PutObjectTagDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author Joy Yang
 */
public interface IBucketTagService extends IService<BucketTag> {

    /**
     * 获取这个Bucket的标签
     * @param bucketName
     * @return
     */
    List<BucketTag> getBucketTag(String bucketName);

    /**
     * 添加Bucket标签
     * @param bucketTags
     * @return
     */
    List<BucketTag> putBucketTag(PutBucketTagDto bucketTags);

    /**
     * 删除对应的Bucket标签
     * @param bucketTags
     * @return
     */
    List<BucketTag> deleteBucketTag(DeleteBucketTagDto bucketTags);
}
