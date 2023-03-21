package ccw.serviceinnovation.oss.manager.authority.bucketacl;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.constant.BucketACLEnum;
import ccw.serviceinnovation.common.constant.ObjectACLEnum;
import ccw.serviceinnovation.oss.mapper.BucketMapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

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


    public BucketACLEnum getBucketAclType(String bucketId){
        Bucket bucket = bucketMapper.selectById(bucketId);
        return BucketACLEnum.getEnum(bucket.getBucketAcl());
    }

    /**
     * 检查bucketACL
     * @param user 请求的用户
     * @param bucket 桶
     * @param apiType 类型
     * @return 返回是否成功
     */
    public Boolean checkBucketAcl(User user, String apiType, Bucket bucket) {
        if (bucket != null) {
            //获取对象的acl权限
            Integer bucketAcl = bucket.getBucketAcl();
            //目标接口是否为读接口
            Boolean read = apiType.equals(AuthorityConstant.API_READ) || apiType.equals(AuthorityConstant.API_LIST);
            //目标接口是否为写接口
            Boolean write = apiType.equals(AuthorityConstant.API_WRITER);
            Boolean mainUserPower = user.getParent() == null || (user.getParent().equals(bucket.getUserId()));
            Boolean ramUserPower = user.getParent() != null && (user.getParent().equals(bucket.getUserId()));
            if (bucketAcl.equals(ObjectACLEnum.PRIVATE.getCode())) {
                //私有权限->只有user本身和RAM用户可以访问
                return mainUserPower;
            } else if (bucketAcl.equals(ObjectACLEnum.PUBLIC_READ.getCode())) {
                return read;
            }if (bucketAcl.equals(ObjectACLEnum.RAM_READ.getCode())) {
                return read && ramUserPower;
            } else if (bucketAcl.equals(ObjectACLEnum.PUBLIC_READ_WRITE.getCode())) {
                return read || write;
            }else if (bucketAcl.equals(ObjectACLEnum.RAM_READ_WRITE.getCode())) {
                return ramUserPower && (read || write);
            }
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
                bucket = bucketMapper.selectOne(MPUtil.queryWrapperEq("name", request.getParameter("bucketName"),"id",1));
                break;
            }
        }
        return bucket;
    }

}
