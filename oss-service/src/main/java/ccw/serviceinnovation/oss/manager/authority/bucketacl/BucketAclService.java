package ccw.serviceinnovation.oss.manager.authority.bucketacl;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.constant.ACLEnum;
import ccw.serviceinnovation.oss.mapper.BucketMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 提供bucketACL的服务
 * 读写权限ACL用于定义用户或用户组被授予的访问权限。收到某个资源的请求后，
 * OSS会检查相应的ACL以验证请求者是否拥有所需的访问权限。
 * 您可以在创建存储空间（Bucket）时设置Bucket ACL，
 * 也可以在创建Bucket后根据自身的业务需求修改Bucket ACL。仅Bucket拥有者可以执行修改Bucket ACL的操作。
 * @author 陈翔
 */
@Component
public class BucketAclService {

    @Autowired
    BucketMapper bucketMapper;



    /**
     * 检查bucketACL
     * @param user 请求的用户
     * @param bucket 桶
     * @param apiType 类型
     * @return 返回是否成功
     */
    public Boolean checkBucketAcl(User user, String apiType, Bucket bucket) {
        if(user.getId().equals(bucket.getUserId())){
            //主用户直接过
            return true;
        }
        if (bucket != null) {
            //获取对象的acl权限
            Integer bucketAcl = bucket.getBucketAcl();
            return checkAcl(user, apiType, bucket.getUserId(), bucketAcl);
        }
        return false;
    }

    public Boolean checkAcl(User user, String apiType, Long resourceUserId,Integer acl){
        //目标接口是否为读接口
        Boolean read = apiType.equals(AuthorityConstant.API_READ) || apiType.equals(AuthorityConstant.API_LIST);
        //目标接口是否为写接口
        Boolean write = apiType.equals(AuthorityConstant.API_WRITER);
        //是这个桶的主人
        Boolean mainUserPower = Objects.equals(user.getId(), resourceUserId);
        //是否为该桶主人的子用户
        Boolean ramUserPower = user.getParent() != null && (user.getParent().equals(resourceUserId));
        if (acl.equals(ACLEnum.PRIVATE.getCode())) {
            //私有权限->只有user本身可以访问
            return mainUserPower;
        } else if (acl.equals(ACLEnum.PUBLIC_READ.getCode())) {
            //公共读->是这个桶的主人 或者 不论身份,只需要是读接口即可
            return mainUserPower || read;
        }if (acl.equals(ACLEnum.RAM_READ.getCode())) {
            //RAM读->是这个桶的主人 或者 身份是RAM,只需要是读接口即可
            return mainUserPower || (ramUserPower && read);
        } else if (acl.equals(ACLEnum.PUBLIC_READ_WRITE.getCode())) {
            //公共可读可写
            //直接返回true即可
            return true;
        }else if (acl.equals(ACLEnum.RAM_READ_WRITE.getCode())) {
            //RAM用户可读可写
            return mainUserPower || ramUserPower;
        }
        return false;
    }


    public Bucket getBucketFromParam(HttpServletRequest request, String[] params){
        Bucket bucket = null;
        for (String param : params) {
            if ("bucketId".equals(param)) {
                bucket = bucketMapper.selectById(Long.valueOf(request.getParameter("bucketId")));
                break;
            } else if ("bucketName".equals(param)) {
                bucket = bucketMapper.selectBucketByName(request.getParameter("bucketName"));
                break;
            }
        }
        if(bucket==null){
            throw new OssException(ResultCode.BUCKET_IS_DEFECT);
        }
        return bucket;
    }

}
