package ccw.serviceinnovation.oss.service.impl;


import ccw.serviceinnovation.common.constant.ACLEnum;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.*;
import ccw.serviceinnovation.oss.pojo.dto.AddBucketDto;
import ccw.serviceinnovation.oss.pojo.vo.FileTypeVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IBucketService;
import ccw.serviceinnovation.oss.service.IObjectService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


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
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private IObjectService objectService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private BucketTagBucketMapper bucketTagBucketMapper;

    @Autowired
    private BucketTagMapper bucketTagMapper;


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
        Integer bucketAcl = addBucketDto.getBucketAcl();
        if(bucketAcl!=null){
            bucket.setBucketAcl(ACLEnum.getEnum(bucketAcl).getCode());
        }else{
            bucket.setBucketAcl(ACLEnum.PRIVATE.getCode());
        }
        bucketMapper.insert(bucket);
        return bucket;
    }



    @Override
    public Boolean deleteBucket(String bucketName) throws Exception {
        Long id = bucketMapper.selectBucketIdByName(bucketName);
        objectService.deleteObjects(bucketName);
        bucketMapper.delete(MPUtil.queryWrapperEq("name", bucketName));
        authorizeMapper.delete(MPUtil.queryWrapperEq("bucket_id", id));
        authorizeUserMapper.delete(MPUtil.queryWrapperEq("bucket_id", id));
        authorizePathMapper.delete(MPUtil.queryWrapperEq("bucket_id", id));
        //删除用户收藏
        userFavoriteMapper.delete(MPUtil.queryWrapperEq("bucket_id", id));
        //删除bucket标签
        bucketTagBucketMapper.deleteTagByBucketId(id);
        bucketTagBucketMapper.delete(MPUtil.queryWrapperEq("bucket_id",id));
        return true;
    }

    @Override
    public RPage<Bucket> getBucketList(Long userId,Integer pageNum,Integer pageSize,String key) {
        RPage<Bucket> rPage = new RPage<>();
        //扎到父级
        User user = userMapper.selectById(userId);
        if(user.getParent()!=null){
            userId = user.getParent();
        }
        rPage.setRows(bucketMapper.selectBucketList(userId,key,(pageNum-1)*pageSize ,pageSize));
        rPage.setTotalCountAndTotalPage(bucketMapper.selectBucketListSize(userId,key));
        return rPage;
    }

    @Override
    public Bucket getBucketInfo(String bucketName) {
        return bucketMapper.selectOne(MPUtil.queryWrapperEq("name", bucketName));
    }


    @Override
    public Boolean updateBucketAcl(String bucketName, Integer bucketAcl) {
        Bucket bucket = bucketMapper.selectBucketByName(bucketName);
        if(bucket==null){
            throw new OssException(ResultCode.BUCKET_IS_DEFECT);
        }else{
            ACLEnum anEnum = ACLEnum.getEnum(bucketAcl);
            if(anEnum==null || anEnum.equals(ACLEnum.DEFAULT)){
                throw new OssException(ResultCode.UNDEFINED);
            }else{
                bucket.setBucketAcl(anEnum.getCode());
                bucketMapper.updateById(bucket);
                return true;
            }
        }
    }

    @Override
    public Boolean updateSecret(String bucketName, Integer secret) {
        throw new OssException(ResultCode.NO_IMPL);
    }

    @Override
    public List<FileTypeVo> getBucketFileType(String bucketName) {
        List<FileTypeVo> fileTypes = bucketMapper.getFileType(bucketName);
        return  fileTypes;
    }

    @Override
    public List<FileTypeVo> getUserBucketFileType(Long userId) {
        List<FileTypeVo> fileTypes = bucketMapper.getUserAllFileType(userId);
        return  fileTypes;
    }

    @Override
    public Boolean reName(String bucketName,String newBucketName) {
        bucketMapper.updateBucketName(bucketName,newBucketName);
        return true;
    }


}
