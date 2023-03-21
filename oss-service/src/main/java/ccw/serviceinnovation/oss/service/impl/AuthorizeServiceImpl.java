package ccw.serviceinnovation.oss.service.impl;
import ccw.serviceinnovation.common.entity.Authorize;
import ccw.serviceinnovation.common.entity.AuthorizePath;
import ccw.serviceinnovation.common.entity.AuthorizeUser;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.AuthorizeMapper;
import ccw.serviceinnovation.oss.mapper.AuthorizePathMapper;
import ccw.serviceinnovation.oss.mapper.AuthorizeUserMapper;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.pojo.dto.PutAuthorizeDto;
import ccw.serviceinnovation.oss.service.IAuthorizeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;


/**
 * bucket授权策略实现类
 * @author 陈翔
 */
@Service
@Transactional(rollbackFor={Exception.class,RuntimeException.class})
public class AuthorizeServiceImpl implements IAuthorizeService {

    @Autowired
    AuthorizeMapper authorizeMapper;

    @Autowired
    AuthorizeUserMapper authorizeUserMapper;

    @Autowired
    AuthorizePathMapper authorizePathMapper;

    @Autowired
    BucketMapper bucketMapper;

    @Override
    public List<Authorize> listAuthorizes(String bucketName) {
        Long id = bucketMapper.selectBucketIdByName(bucketName);
        return authorizeMapper.selectByMap(MPUtil.getMap("bucket_id", id));
    }

    /**
     * 插入多条授权策略的授权目标
     * @param arr
     * @param authorizeId
     * @return
     */
    public Boolean insertUsers(String[] arr,Long authorizeId,Long bucketId){
        for (String id : arr) {
            AuthorizeUser authorizeUser = new AuthorizeUser();
            Long userId = Long.valueOf(id);
            authorizeUser.setUserId(userId);
            authorizeUser.setAuthorizeId(authorizeId);
            authorizeUser.setBucketId(bucketId);
            authorizeUserMapper.insert(authorizeUser);
        }
        return true;
    }

    /**
     * 插入多条授权策略的授权路径
     * @param arr
     * @param authorizeId
     * @return
     */
    public Boolean insertPath(String[] arr,Long authorizeId,Long bucketId){
        for (String path : arr) {
            //最后一个字符替换成%
            if(path.charAt(path.length()-1)=='*'){
                path = path.substring(0,path.length()-1)+"%";
            }
            AuthorizePath authorizePath = new AuthorizePath();
            authorizePath.setAuthorizeId(authorizeId);
            authorizePath.setPath(path);
            authorizePath.setBucketId(bucketId);
            authorizePathMapper.insert(authorizePath);
        }
        return true;
    }

    @Override
    public Boolean putAuthorize(PutAuthorizeDto putAuthorizeDto, String bucketName, Long authorizeId) {
        String[] arr1 = putAuthorizeDto.getSonUser();
        String[] arr2 = putAuthorizeDto.getOtherUser();
        String[] arr3 = Arrays.copyOf(arr1, arr1.length + arr2.length);
        System.arraycopy(arr2, 0, arr3, arr1.length, arr2.length);
        Long bucketId = null;
        if(authorizeId == null){
            //create a authorize
            bucketId = bucketMapper.selectBucketIdByName(bucketName);
            Authorize authorize = new Authorize();
            authorize.setBucketId(bucketId);
            BeanUtils.copyProperties(putAuthorizeDto, authorize);
            authorizeMapper.insert(authorize);
            return insertUsers(arr3, authorize.getId(),bucketId) && insertPath(putAuthorizeDto.getPaths(), authorize.getId(),bucketId);
        }else{
            //update a authorize
            bucketId = bucketMapper.selectBucketIdByName(bucketName);
            Authorize authorize = new Authorize();
            authorize.setBucketId(bucketId);
            BeanUtils.copyProperties(putAuthorizeDto, authorize);
            int update = authorizeMapper.update(authorize, MPUtil.queryWrapperEq("id",authorizeId));
            int delete1 = authorizeUserMapper.delete(MPUtil.queryWrapperEq("authorize_id", authorizeId));
            int delete2 = authorizePathMapper.delete(MPUtil.queryWrapperEq("authorize_id",authorizeId));
            if(!(update > 0 && delete1 > 0 && delete2 > 0)){
                return false;
            }
        }
        return insertUsers(arr3, authorizeId,bucketId) && insertPath(putAuthorizeDto.getPaths(), authorizeId,bucketId);
    }

    @Override
    public Boolean deleteAuthorize(String bucketName, Long authorizeId) {
        Long id = bucketMapper.selectBucketIdByName(bucketName);
        int delete1 = authorizeMapper.delete(MPUtil.queryWrapperEq("id", authorizeId, "bucket_id", id));
        int delete2 = authorizeUserMapper.delete(MPUtil.queryWrapperEq("authorize_id", authorizeId));
        int delete3 = authorizePathMapper.delete(MPUtil.queryWrapperEq("authorize_id", authorizeId));
        return delete1>0 && delete2>0 && delete3>0;
    }

}
