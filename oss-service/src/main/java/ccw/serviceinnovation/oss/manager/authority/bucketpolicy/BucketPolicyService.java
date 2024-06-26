package ccw.serviceinnovation.oss.manager.authority.bucketpolicy;

import ccw.serviceinnovation.common.constant.AuthorizeOperationEnum;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.mapper.AuthorizeMapper;
import ccw.serviceinnovation.oss.mapper.AuthorizePathMapper;
import ccw.serviceinnovation.oss.mapper.AuthorizeUserMapper;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.pojo.bo.AuthorizeBo;
import lombok.Data;
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

    @Autowired
    BucketMapper bucketMapper;

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
     * @return  返回bool最终值
     */
    public Boolean updateValue(Value bool, boolean check){
        log.info("updateValue {},{}",bool,check);
        //满足前置条件
        if (check && bool.getFlag()==null){
            //bool没有被赋值过则可以被赋值为true 如果存在允许策略且之前没有被赋值过拒绝策略
            bool.setFlag(true);
            return true;
        }else if(!check){
            //check为false 只要出现拒绝策略直接拒绝
            bool.setFlag(false);
            return false;
        }
        return bool.getFlag();
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
        //每个AuthorizeOperationEnum对应了可以允许哪种哪个接口
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
     * @param value 需要赋值的boolean类型
     * @param front 前置条件
     */
    public void checkOperationType(AuthorizeOperationEnum operation, String type, Value value, boolean front){
        if(front){
            if(Objects.equals(operation, ACCESS_DENIED)){
                //拒绝访问      待赋值      拒绝
                updateValue(value,false);
            }else if(Objects.equals(operation, FULL_CONTROL)){
                //完全控制
                updateValue(value,true);
            }else{
                //这个效力 可以作用于目标接口时
                if(checkInclude(operation, type)){
                    updateValue(value,true);
                }else{
                    updateValue(value,false);
                }
            }
        }
    }

    @Data
    static class Value{
        Boolean flag;
        public Value(Boolean flag){
            this.flag = flag;
        }
    }

    /**
     * 检查这个路径是否可以访问
     * @param accessUserId 访问的人的id
     * @param accessBucketId  访问的人的桶id
     * @param accessPath    此次访问的路径
     * @param type 目标接口的类型 OssApi()
     * @return
     */
    public Boolean check(Long accessUserId, Long accessBucketId, String accessPath, String type){
        Bucket bucket = bucketMapper.selectById(accessBucketId);
        Long sourceUser = bucket.getUserId();
        if(sourceUser.equals(accessUserId)){
            //bucket的拥有者拥有最高权限
            return true;
        }
        //先置为null 拒绝就设置false 允许就设置false
        Value value = new Value(null);
        //先left join 三表连接
        List<AuthorizeBo> authorizeBos = selectAuthorize(accessUserId,accessBucketId, accessPath);
        System.out.println("size:"+authorizeBos.size());
        //遍历所有涉及到 这个用户 这个桶 和 这个路径的 bucketPolicy,
        //只要进了for循环,必然是允许或者拒绝
        for (AuthorizeBo authorizeBo : authorizeBos) {
            Boolean pathIsAll = authorizeBo.getPathIsAll();
            Boolean userIsAll = authorizeBo.getUserIsAll();
            Long userId = authorizeBo.getUserId();
            String path = authorizeBo.getPath();
            log.info("{},{},{}",authorizeBo.getId(),userId,path);
            //这个policy的效力 ONLY_READ, ONLY_READ_INCLUDE_LIST, READ_AND_WRITER, FULL_CONTROL, ACCESS_DENIED
            AuthorizeOperationEnum operation = AuthorizeOperationEnum.getEnum(authorizeBo.getOperation());
            if(pathIsAll && userIsAll){
                //任何用户任何路径都匹配 效力  目标接口类型 待赋值   必然可以进入判断
                checkOperationType(operation, type, value, true);
            }else if(!pathIsAll && userIsAll){
                //任何用户匹配指定路径 用户必然为空 需要有路径才进行判断
                checkOperationType(operation, type, value,path!=null);
            }else if(pathIsAll && !userIsAll){
                //指定用户匹配所有路径 路径必然为空 需要有用户才进行判断
                checkOperationType(operation, type, value,userId!=null);
            }else if(!pathIsAll && !userIsAll){
                //指定用户匹配指定路径
                checkOperationType(operation, type, value,path!=null && userId!=null);
            }
        }
        if(!value.getFlag()){
            throw new OssException(ResultCode.BUCKET_POLICY_BLOCK);
        }
        return value.getFlag();
    }

    public static void alter(Boolean value){
        value = true;
    }

    public static void main(String[] args) {

    }
}
