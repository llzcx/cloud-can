package ccw.serviceinnovation.oss.service.impl;
import ccw.serviceinnovation.common.entity.Authorize;
import ccw.serviceinnovation.common.entity.AuthorizePath;
import ccw.serviceinnovation.common.entity.AuthorizeUser;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.*;
import ccw.serviceinnovation.oss.pojo.dto.PutAuthorizeDto;
import ccw.serviceinnovation.oss.pojo.vo.AuthorizeVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IAuthorizeService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AuthorizeServiceImpl implements IAuthorizeService {

    @Autowired
    AuthorizeMapper authorizeMapper;

    @Autowired
    AuthorizeUserMapper authorizeUserMapper;

    @Autowired
    AuthorizePathMapper authorizePathMapper;

    @Autowired
    BucketMapper bucketMapper;

    @Autowired
    UserMapper userMapper;

    @Override
    public RPage<AuthorizeVo> listAuthorizes(String bucketName, Integer pageNum, Integer pageSize) {
        Bucket bucket = bucketMapper.selectBucketByName(bucketName);
        RPage<AuthorizeVo> rPage = new RPage<>();
        rPage.setTotalCountAndTotalPage(authorizeMapper.selectAuthorizeListCount(bucketName));
        List<AuthorizeVo> list = authorizeMapper.selectAuthorizeList(bucketName, pageSize * (pageNum - 1), pageSize);
        rPage.setRows(list);
        for (AuthorizeVo item : list) {
            List<AuthorizePath> paths = authorizePathMapper.selectList(MPUtil.queryWrapperEq("authorize_id", item.getId()));
            String[] PathArr = new String[paths.size()];
            for (int i = 0; i < paths.size(); i++) {
                String pathName = paths.get(i).getPath();
                if(pathName.charAt(pathName.length()-1)=='%'){
                    pathName = pathName.substring(0,pathName.length()-1)+"*";
                }
                PathArr[i] = pathName;
            }
            item.setPaths(PathArr);
            List<String> authorizeUsers = authorizeUserMapper.selectAuthorizeOtherUserList(bucket.getUserId(),item.getId());
            String[] mainArr = new String[authorizeUsers.size()];
            for (int i = 0; i < authorizeUsers.size(); i++) {
                mainArr[i] = authorizeUsers.get(i);
            }
            item.setSonUser(mainArr);
            List<String> sonUsers = authorizeUserMapper.selectAuthorizeRAMUserList(bucket.getUserId(),item.getId());
            String[] sonArr = new String[sonUsers.size()];
            for (int i = 0; i < sonUsers.size(); i++) {
                sonArr[i] = sonUsers.get(i);
            }
            item.setSonUser(sonArr);
        }
        return rPage;
    }

    /**
     * 插入多条授权策略的授权目标
     * @param arr
     * @param authorizeId
     * @return
     */
    public Boolean insertUsers(String[] arr,Long authorizeId,Long bucketId){
        for (String name : arr) {
            AuthorizeUser authorizeUser = new AuthorizeUser();
            Long userId = userMapper.selectUserIdByName(name);
            if(userId==null){
                throw new OssException(ResultCode.USER_IS_NULL);
            }
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

    public static void main(String[] args) {
        String[] arr1 = new String[]{"1","2"};
        String[] arr2 = new String[]{"3","4"};;
        String[] arr3 = Arrays.copyOf(arr1, arr1.length + arr2.length);
        System.arraycopy(arr2, 0, arr3, arr1.length, arr2.length);
        System.out.println(JSONObject.toJSON(arr3));
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
            log.info("返回的:{}",authorize.getId());
            return insertUsers(arr3, authorize.getId(),bucketId) && insertPath(putAuthorizeDto.getPaths(), authorize.getId(),bucketId);
        }else{
            //update a authorize
            bucketId = bucketMapper.selectBucketIdByName(bucketName);
            Authorize authorize = new Authorize();
            authorize.setBucketId(bucketId);
            authorize.setId(authorizeId);
            BeanUtils.copyProperties(putAuthorizeDto, authorize);
            int update = authorizeMapper.updateById(authorize);
            int delete1 = authorizeUserMapper.delete(MPUtil.queryWrapperEq("authorize_id", authorizeId));
            int delete2 = authorizePathMapper.delete(MPUtil.queryWrapperEq("authorize_id",authorizeId));
        }
        return insertUsers(arr3, authorizeId,bucketId) && insertPath(putAuthorizeDto.getPaths(), authorizeId,bucketId);
    }

    @Override
    public Boolean deleteAuthorize(String bucketName, Long authorizeId) {
        Long id = bucketMapper.selectBucketIdByName(bucketName);
        int delete1 = authorizeMapper.deleteById(authorizeId);
        int delete2 = authorizeUserMapper.delete(MPUtil.queryWrapperEq("authorize_id", authorizeId));
        int delete3 = authorizePathMapper.delete(MPUtil.queryWrapperEq("authorize_id", authorizeId));
        return delete1>0 && delete2>0 && delete3>0;
    }

}
