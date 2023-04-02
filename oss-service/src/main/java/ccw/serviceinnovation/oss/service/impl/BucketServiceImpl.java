package ccw.serviceinnovation.oss.service.impl;


import ccw.serviceinnovation.common.constant.ObjectACLEnum;
import ccw.serviceinnovation.common.constant.SecretEnum;
import ccw.serviceinnovation.common.constant.StorageTypeEnum;
import ccw.serviceinnovation.common.entity.AuthorizePath;
import ccw.serviceinnovation.common.entity.AuthorizeUser;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.*;
import ccw.serviceinnovation.oss.pojo.dto.AddBucketDto;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IBucketService;
import ccw.serviceinnovation.oss.service.IObjectService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.Result;
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
    private AuthorizeMapper authorizeMapper;

    @Autowired
    private AuthorizePathMapper authorizePathMapper;

    @Autowired
    private AuthorizeUserMapper authorizeUserMapper;

    @Autowired
    private IObjectService objectService;

    @Autowired
    private UserMapper userMapper;


    @Override
    public Bucket createBucket(AddBucketDto addBucketDto, Long userId)  {
        Bucket bucket = new Bucket();
        if(addBucketDto.getBucketName()==null || "".equals(addBucketDto.getBucketName().trim())){
            throw new OssException(ResultCode.BUCKET_NAME_NULL);
        }
        bucket.setName(addBucketDto.getBucketName());
        User user = userMapper.selectById(userId);
        if(user.getParent()!=null){
            bucket.setUserId(user.getParent());
        }else{
            bucket.setUserId(userId);
        }
        bucket.setCreateTime(DateUtil.now());
        bucket.setUpdateTime(DateUtil.now());
        Integer secret = addBucketDto.getSecret();
        if(secret!=null){
            bucket.setSecret(SecretEnum.getEnum(secret).getCode());
        }
        Integer bucketAcl = addBucketDto.getBucketAcl();
        if(bucketAcl!=null){
            bucket.setBucketAcl(ObjectACLEnum.getEnum(bucketAcl).getCode());
        }else{
            bucket.setBucketAcl(ObjectACLEnum.PRIVATE.getCode());
        }
        Integer storageType = addBucketDto.getStorageType();
        if(storageType!=null){
            bucket.setStorageLevel(StorageTypeEnum.getEnum(storageType).getCode());
        }else{
            bucket.setStorageLevel(StorageTypeEnum.STANDARD.getCode());
        }
        bucketMapper.insert(bucket);
        return bucket;
    }

    @Override
    public Boolean deleteBucket(String bucketName) throws Exception {
        Long id = bucketMapper.selectBucketIdByName(bucketName);
        bucketMapper.delete(MPUtil.queryWrapperEq("name", bucketName));
        objectService.deleteObjects(bucketName);
        authorizeMapper.delete(MPUtil.queryWrapperEq("bucket_id", id));
        authorizeUserMapper.delete(MPUtil.queryWrapperEq("bucket_id", id));
        authorizePathMapper.delete(MPUtil.queryWrapperEq("bucket_id", id));
        return true;
    }

    @Override
    public RPage<Bucket> getBucketList(Long userId,Integer pageNum,Integer pageSize,String key) {
        RPage<Bucket> rPage = new RPage<>();
        rPage.setRows(bucketMapper.selectBucketList(userId,key,(pageNum-1)*pageSize ,pageSize));
        rPage.setTotalCountAndTotalPage(bucketMapper.selectBucketListSize(userId,key));
        return rPage;
    }

    @Override
    public Bucket getBucketInfo(String bucketName) {
        return bucketMapper.selectOne(MPUtil.queryWrapperEq("name", bucketName));
    }
}
