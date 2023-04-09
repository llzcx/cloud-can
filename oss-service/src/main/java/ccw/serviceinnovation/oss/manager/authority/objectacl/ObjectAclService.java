package ccw.serviceinnovation.oss.manager.authority.objectacl;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.constant.ACLEnum;
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
        if(user.getId().equals(bucket.getUserId())){
            //主用户直接过
            return true;
        }
        if (ossObject != null && bucket!=null) {
            //获取对象的acl权限
            if(!ossObject.getObjectAcl().equals(ACLEnum.DEFAULT.getCode())){
                Integer objectAcl = ossObject.getObjectAcl();
                bucketAclService.checkAcl(user, apiType, bucket.getUserId(), objectAcl);
            }else{
                bucketAclService.checkAcl(user, apiType, bucket.getUserId(), bucket.getBucketAcl());
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
        if(bucket==null){
            throw new OssException(ResultCode.BUCKET_IS_DEFECT);
        }

        OssObject ossObject = ossObjectMapper.selectObjectByName(bucket.getName(), objectName);
        if(ossObject==null){
            throw new OssException(ResultCode.OBJECT_IS_DEFECT);
        }
        return ossObject;
    }
}
