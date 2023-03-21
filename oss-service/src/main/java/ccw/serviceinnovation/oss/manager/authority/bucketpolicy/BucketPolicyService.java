package ccw.serviceinnovation.oss.manager.authority.bucketpolicy;

import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.constant.AuthorizeOperationEnum;
import ccw.serviceinnovation.oss.mapper.AuthorizeMapper;
import ccw.serviceinnovation.oss.mapper.AuthorizePathMapper;
import ccw.serviceinnovation.oss.mapper.AuthorizeUserMapper;
import ccw.serviceinnovation.oss.pojo.bo.AuthorizeBo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static ccw.serviceinnovation.common.constant.AuthorizeOperationEnum.ACCESS_DENIED;
import static ccw.serviceinnovation.common.constant.AuthorizeOperationEnum.FULL_CONTROL;

/**
 * @author 陈翔
 */
@Component
@Slf4j
public class BucketPolicyService {

    @Autowired
    AuthorizeMapper authorizeMapper;

    @Autowired
    AuthorizePathMapper authorizePathMapper;

    @Autowired
    AuthorizeUserMapper authorizeUserMapper;

    /**
     * 获取一个桶所有权限
     * @param userId
     * @param bucketId
     */
    public List<AuthorizeBo> selectAuthorize(Long userId, Long bucketId, String path){
        return authorizeMapper.selectAuthorize(userId, bucketId, path);
    }

    /**
     * 开关
     * 如果判断值为true,只有目标值为null(从来没有赋值过)时赋值
     * 如果判断值为false则给目标赋值
     * @param bool 目标
     * @param check 判断值
     * @param front 前置值
     * @return  返回bool最终值
     */
    public Boolean changeBoolean(Boolean bool,boolean check,boolean front){
        if(front){
            //满足前置条件
            if (check && bool==null){
                //bool没有被赋值过则可以被赋值为true
                return bool = true;
            }else if(!check){
                //check为false 无论
                return bool = false;
            }
        }
        return bool;
    }

    /**
     * 检查操作枚举类是否包含了type (type 为空时有2种可能:1.type为空 2.操作为空)
     * @param operation
     * @param type
     * @return
     */
    private Boolean checkInclude(AuthorizeOperationEnum operation, String type) {
        if(type==null) {
            throw new OssException(ResultCode.NULL_POINT_EXCEPTION);
        }
        if(operation.equals(FULL_CONTROL) || operation.equals(ACCESS_DENIED)){
            throw new OssException(ResultCode.NULL_POINT_EXCEPTION);
        }
        for (String s : operation.getOperation()) {
            if(s.equals(type)){
                return true;
            }
        }
        return false;
    }
    /**
     * 检查操作与type的关系
     * @param operation 待检查的操作
     * @param type 与之是否匹配的authorize_type
     * @param flag 需要赋值的boolean类型
     * @param front 前置条件
     */
    public void checkOperationType(AuthorizeOperationEnum operation, String type, Boolean flag, boolean front){
        if(Objects.equals(operation, ACCESS_DENIED)){
            changeBoolean(flag,false,front);
        }else if(Objects.equals(operation, FULL_CONTROL)){
            changeBoolean(flag,true,front);
        }else{
            if(checkInclude(operation, type)){
                changeBoolean(flag,true,front);
            }
        }
    }

    /**
     * 检查这个路径是否可以访问
     * @param accessUserId
     * @param accessBucketId
     * @param accessPath
     * @param type
     * @return
     */
    public Boolean check(Long accessUserId, Long accessBucketId, String accessPath, String type) {
        Boolean flag = null;
        List<AuthorizeBo> authorizeBos = selectAuthorize(accessUserId,accessBucketId, accessPath);
        for (AuthorizeBo authorizeBo : authorizeBos) {
            Boolean pathIsAll = authorizeBo.getPathIsAll();
            Boolean userIsAll = authorizeBo.getUserIsAll();
            Long userId = authorizeBo.getUserId();
            String path = authorizeBo.getPath();
            AuthorizeOperationEnum operation = AuthorizeOperationEnum.getEnum(authorizeBo.getOperation());
            if(pathIsAll && userIsAll){
                //任何用户任何路径都匹配
                checkOperationType(operation, type, flag, true);
            }else if(!pathIsAll && userIsAll){
                //任何用户匹配指定路径
                checkOperationType(operation, type, flag,path!=null);
            }else if(pathIsAll && !userIsAll){
                //指定用户匹配所有路径
                checkOperationType(operation, type, flag,userId!=null);
            }else if(!pathIsAll && !userIsAll){
                //指定用户匹配指定路径
                checkOperationType(operation, type, flag,path!=null && userId!=null);
            }
        }
        return flag;
    }
}
