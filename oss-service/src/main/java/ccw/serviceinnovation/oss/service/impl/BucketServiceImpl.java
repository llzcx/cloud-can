package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.constant.BucketACLEnum;
import ccw.serviceinnovation.common.constant.StorageTypeEnum;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.pojo.dto.AddBucketDto;
import ccw.serviceinnovation.oss.service.IBucketService;
import ccw.serviceinnovation.oss.service.IObjectService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
 * @author 陈翔
 */
@Service
@Slf4j
@Transactional(rollbackFor={Exception.class,RuntimeException.class})
public class BucketServiceImpl extends ServiceImpl<BucketMapper, Bucket> implements IBucketService {



    @Autowired
    private BucketMapper bucketMapper;

    @Autowired
    private IObjectService objectService;


    @Override
    public Bucket createBucket(AddBucketDto addBucketDto, Long userId)  {
        Bucket bucket = new Bucket();
        bucket.setName(addBucketDto.getBucketName());
        bucket.setUserId(userId);
        bucket.setCreateTime(DateUtil.now());
        bucket.setUpdateTime(DateUtil.now());
        bucket.setBucketAcl(BucketACLEnum.PRIVATE.getCode());
        StorageTypeEnum storageTypeEnum = StorageTypeEnum.getEnum(addBucketDto.getStorageType());
        Integer value = storageTypeEnum==null?StorageTypeEnum.STANDARD.getCode():storageTypeEnum.getCode();
        bucket.setStorageLevel(value);
        bucketMapper.insert(bucket);
        return bucket;
    }

    @Override
    public Boolean deleteBucket(String bucketName) throws Exception {
        bucketMapper.delete(MPUtil.queryWrapperEq("name", bucketName));
        objectService.deleteObjects(bucketName);
        return true;
    }

    @Override
    public List<Bucket> getBucketList(Long userId) {
        Map<String,Object> mp = MPUtil.getMap("user_id",userId);
        return bucketMapper.selectByMap(mp);
    }

    @Override
    public Bucket getBucketInfo(String bucketName) {
        return bucketMapper.selectOne(MPUtil.queryWrapperEq("name", bucketName));
    }
}
