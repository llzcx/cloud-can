package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.BucketTag;
import ccw.serviceinnovation.common.entity.BucketTagBucket;
import ccw.serviceinnovation.common.entity.ObjectTag;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.BucketTagBucketMapper;
import ccw.serviceinnovation.oss.mapper.BucketTagMapper;
import ccw.serviceinnovation.oss.service.IBucketTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Joy Yang
 */
@Service
public class BucketTagServiceImpl extends ServiceImpl<BucketTagMapper, BucketTag> implements IBucketTagService {

    @Autowired
    private BucketTagMapper bucketTagMapper;

    @Autowired
    private BucketTagBucketMapper bucketTagBucketMapper;

    @Autowired
    private BucketMapper bucketMapper;

    @Override
    public List<BucketTag> getBucketTag(String bucketName) {
        Long bucketIdByName = bucketMapper.selectBucketIdByName(bucketName);
        List<BucketTag> bucketTags = bucketTagMapper.getBucketTag(bucketIdByName);
        return bucketTags;
    }

    @Override
    public List<BucketTag> putBucketTag(String bucketName, List<BucketTag> bucketTags) {
        Long bucketId = bucketMapper.selectBucketIdByName(bucketName);

        List<BucketTag> bucketTagsOld = getBucketTag(bucketName);

        for (BucketTag bucketTag : bucketTags) {

            //判断是否有相同的key
            for (BucketTag bucketTagOld : bucketTagsOld) {
                if (bucketTagOld.getKey().equals(bucketTag.getKey())){
                    return null;
                }
            }

            BucketTag newBucketTag = new BucketTag();
            newBucketTag.setKey(bucketTag.getKey());
            newBucketTag.setValue(bucketTag.getValue());
            bucketTagMapper.insertTag(newBucketTag);

            BucketTagBucket bucketTagBucket = new BucketTagBucket();
            bucketTagBucket.setBucketId(bucketId);
            bucketTagBucket.setTagId(newBucketTag.getId());
            bucketTagBucketMapper.insert(bucketTagBucket);
        }

        List<BucketTag> newBucketTagsOld = getBucketTag(bucketName);
        return newBucketTagsOld;
    }

    @Override
    public Boolean deleteBucketTag(String bucketName, Long tagId) {
        Long bucketId = bucketMapper.selectBucketIdByName(bucketName);
        int delete = bucketTagBucketMapper.delete(MPUtil.queryWrapperEq("bucket_id",bucketId,"tag_id",tagId));
        int deleteTag = bucketTagMapper.delete(MPUtil.queryWrapperEq("id",tagId));
        return delete>0 && deleteTag>0;
    }
}
