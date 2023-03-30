package ccw.serviceinnovation.oss.manager.authority.objectacl;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.constant.ObjectACLEnum;
import ccw.serviceinnovation.oss.manager.authority.bucketacl.BucketAclService;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.OssObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 提供ObjectACL的服务
 * @author 陈翔
 */
@Component
public class ObjectAclService {

    @Autowired
    OssObjectMapper ossObjectMapper;

    @Autowired
    BucketMapper bucketMapper;

    @Autowired
    BucketAclService bucketAclService;
    /**
     * 检查objectACL
     * @param user 请求的用户
     * @param ossObject 请求的参数
     * @param apiType 接口类型
     * @return 返回是否成功
     */
    public Boolean checkObjectAcl(Bucket bucket, User user, String apiType, OssObject ossObject) {
        if (ossObject != null && bucket!=null) {
            //获取对象的acl权限
            Integer objectAcl = ossObject.getObjectAcl();
            //目标接口是否为读接口
            Boolean read = apiType.equals(AuthorityConstant.API_READ) || apiType.equals(AuthorityConstant.API_LIST);
            //目标接口是否为写接口
            Boolean write = apiType.equals(AuthorityConstant.API_WRITER);
            Boolean mainUserPower = user.getParent() == null || (user.getParent().equals(bucket.getUserId()));
            Boolean ramUserPower = user.getParent() != null && (user.getParent().equals(bucket.getUserId()));
            if (objectAcl.equals(ObjectACLEnum.PRIVATE.getCode())) {
                //私有权限->只有user本身和RAM用户可以访问
                return mainUserPower;
            } else if (objectAcl.equals(ObjectACLEnum.PUBLIC_READ.getCode())) {
                return read;
            }if (objectAcl.equals(ObjectACLEnum.RAM_READ.getCode())) {
                return read && ramUserPower;
            } else if (objectAcl.equals(ObjectACLEnum.PUBLIC_READ_WRITE.getCode())) {
                return read || write;
            }else if (objectAcl.equals(ObjectACLEnum.RAM_READ_WRITE.getCode())) {
                return ramUserPower && (read || write);
            }  else if (objectAcl.equals(ObjectACLEnum.DEFAULT.getCode())) {
                //调用bucketAcl进行验证
                return bucketAclService.checkBucketAcl( user, apiType,bucket);
            }
        }
        return false;
    }

    public OssObject getObjectFromParam(HttpServletRequest request,String[] params){
        Bucket bucket = null;
        String objectName = request.getParameter("objectName");
        for (String param : params) {
            if ("objectId".equals(param)) {
                return ossObjectMapper.selectById(Long.valueOf(request.getParameter("objectId")));
            } else if ("bucketName".equals(param)) {
                bucket = bucketMapper.selectBucketByName(request.getParameter("bucketName"));
            }else  if ("bucketId".equals(param)) {
                bucket = bucketMapper.selectById(Long.valueOf(request.getParameter("bucketId")));
            }
        }
        if(objectName==null || bucket==null){
            return null;
        }
        return ossObjectMapper.selectObjectByName(bucket.getName(), objectName);
    }
}
