package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.BucketTag;
import ccw.serviceinnovation.common.entity.BucketTagBucket;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.BucketTagBucketMapper;
import ccw.serviceinnovation.oss.mapper.BucketTagMapper;
import ccw.serviceinnovation.oss.pojo.dto.DeleteBucketTagDto;
import ccw.serviceinnovation.oss.pojo.dto.PutBucketTagDto;
import ccw.serviceinnovation.oss.pojo.dto.TagDto;
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
    public List<BucketTag> putBucketTag(PutBucketTagDto bucketTags) {
        Long bucketId = bucketMapper.selectBucketIdByName(bucketTags.getBucketName());

        List<BucketTag> bucketTagsOld = getBucketTag(bucketTags.getBucketName());

        for (TagDto bucketTag : bucketTags.getTags()) {
            int flag = 0;
            //判断是否有相同的key
            for (BucketTag bucketTagOld : bucketTagsOld) {

                if (bucketTagOld.getKey().equals(bucketTag.getKey())){
                    flag = 1;
                }
            }

            if (flag==1){
                continue;
            }

            BucketTag newBucketTag = new BucketTag();
            newBucketTag.setKey(bucketTag.getKey());
            newBucketTag.setValue(bucketTag.getValue());
            bucketTagMapper.insertTag(newBucketTag);

            BucketTagBucket bucketTagBucket = new BucketTagBucket();
            bucketTagBucket.setBucketId(bucketId);
            bucketTagBucket.setTagId(newBucketTag.getId());
            bucketTagBucketMapper.insertTag(bucketTagBucket.getTagId(),bucketTagBucket.getBucketId());
        }

        List<BucketTag> newBucketTagsOld = getBucketTag(bucketTags.getBucketName());
        return newBucketTagsOld;
    }

    @Override
    public List<BucketTag> deleteBucketTag(DeleteBucketTagDto bucketTags) {
        Long bucketId = bucketMapper.selectBucketIdByName(bucketTags.getBucketName());

        for (BucketTag bucketTag : bucketTags.getBucketTags()) {
            bucketTagBucketMapper.delete(MPUtil.queryWrapperEq("bucket_id",bucketId,"tag_id",bucketTag));
            bucketTagMapper.delete(MPUtil.queryWrapperEq("id",bucketTag.getId()));
        }

        List<BucketTag> newBucketTagsOld = getBucketTag(bucketTags.getBucketName());
        return newBucketTagsOld;
    }
}
