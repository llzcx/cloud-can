package ccw.serviceinnovation.oss.manager.authority;

import ccw.serviceinnovation.common.entity.Backup;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.ControllerUtils;
import ccw.serviceinnovation.oss.manager.authority.accesskey.ObjectAccessKeyService;
import ccw.serviceinnovation.oss.manager.authority.bucketacl.BucketAclService;
import ccw.serviceinnovation.oss.manager.authority.bucketpolicy.BucketPolicyService;
import ccw.serviceinnovation.oss.manager.authority.identity.IdentityAuthentication;
import ccw.serviceinnovation.oss.manager.authority.objectacl.ObjectAclService;
import ccw.serviceinnovation.oss.mapper.BackupMapper;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.OssObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static ccw.serviceinnovation.common.constant.AuthorityConstant.*;
import static ccw.serviceinnovation.common.constant.RequestHeadersConstant.ACCESS_KEY;

/**
 * 拦截器类
 * @author 陈翔
 */
@Component
@Slf4j
public class RequestInterceptor implements HandlerInterceptor {

    @Autowired
    BucketMapper bucketMapper;

    @Autowired
    IdentityAuthentication identityAuthentication;

    @Autowired
    BucketAclService bucketAclService;

    @Autowired
    ObjectAclService objectAclService;

    @Autowired
    BucketPolicyService bucketPolicyService;

    @Autowired
    ObjectAccessKeyService objectAccessKeyService;

    @Autowired
    BackupMapper backupMapper;

    @Autowired
    OssObjectMapper ossObjectMapper;




    /**
     * 忽略拦截的url
     */
    private final List<String> urls = Arrays.asList(
            "/error",
            "/user/login",
            "/user/register",
            "/swagger-ui",
            "/swagger-resources",
            "/v3/api-docs",
            "/test/demo"
    );

    /**
     * 默认放行的资源
     * @param httpServletRequest
     * @param handler
     * @return
     */
    public Boolean checkCanPassByStatic(HttpServletRequest httpServletRequest,Object handler){
        if(HttpMethod.OPTIONS.toString().equals(httpServletRequest.getMethod())) {
            //options请求.放行
            return true;
        }
        if(!(handler instanceof HandlerMethod)){
            //不是映射到方法直接通过
            return true;
        }
        //不拦截的路径
        String uri = httpServletRequest.getRequestURI();
        for (String url : urls) {
            if(uri.contains(url)) {
                log.info("Releasable Path:"+url);
                return true;
            }
        }
        return false;
    }



    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("-------------------------Request start:"+request.getRequestURI());
        response.setContentType("application/json; charset=utf-8");
        /*-------------------是否为可放行资源-------------------*/
        if(checkCanPassByStatic(request,handler)){
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;Method method = handlerMethod.getMethod();OssApi ossApi = method.getAnnotation(OssApi.class);LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        //获取接口的方法名列表
        String[] params = u.getParameterNames(method);
        String readAndWriteType = ossApi.type();
        String target = ossApi.target();
        String accessKey = request.getParameter(ACCESS_KEY);
        AuthInfo authInfo = new AuthInfo();
        AuthContext.set(authInfo);
        /*-------------------验证令牌-------------------*/
        User user = identityAuthentication.verify(request);
        authInfo.setUser(user);
        //目标接口含有ossApi注解,并且接口含有参数
        if(target.equals(API_BUCKET)){
            /*-------------------验证bucketAcl-------------------*/
            Bucket bucket = bucketAclService.getBucketFromParam(request, params);
            authInfo.setBucket(bucket);
            log.debug("bucket is {}",bucket.getName());
            return ControllerUtils.writeIfReturn(response, ResultCode.BUCKET_ACL_BLOCK,
                    bucketAclService.checkBucketAcl(user, readAndWriteType, bucket));
        }else if(ossApi.target().equals(API_OBJECT)){
            if(readAndWriteType.equals(API_BACK_UP)){
                //源对象需要有读权限
                String sourceBucketName = request.getParameter("bucketName");
                String sourceObjectName = request.getParameter("objectName");
                readAndWriteType = API_READ;
                //目标桶需要有写权限
                String targetBucketName = request.getParameter("targetBucketName");
                bucketAclService.checkBucketAcl(user, API_WRITER, bucketMapper.selectBucketByName(targetBucketName));
            }else if(readAndWriteType.equals(API_BACKUP_RECOVERY)){
                String targetBucketName = request.getParameter("bucketName");
                String targetObjectName = request.getParameter("objectName");
                Backup backup = backupMapper.selectBackupByTarget(targetBucketName, targetObjectName);
                Long sourceObjectId = backup.getSourceObjectId();
                Long targetObjectId = backup.getTargetObjectId();
                //目标对象需要有读权限
                readAndWriteType = API_READ;
                //源桶需要有写权限
                OssObject ossObject = ossObjectMapper.selectById(sourceObjectId);
                Long sourceBucketId = ossObject.getBucketId();
                bucketAclService.checkBucketAcl(user, API_WRITER, bucketMapper.selectById(sourceBucketId));
            }
            OssObject ossObject = objectAclService.getObjectFromParam(request, params);
            authInfo.setOssObject(ossObject);
            if(readAndWriteType.equals(API_READ) &&  accessKey!=null){
                if(objectAccessKeyService.handle(ossObject,accessKey)){
                    return true;
                }
            }
            log.debug("object is {}",ossObject.getName());
            Bucket bucket = bucketMapper.selectById(ossObject.getBucketId());
            /*-------------------判断bucketPolicy-------------------*/
            String accessPath = ossObject.getName();
            Boolean check = bucketPolicyService.check(user.getId(), bucket.getId(), accessPath, readAndWriteType);
            if(check==null){
                return ControllerUtils.writeIfReturn(response, ResultCode.BUCKET_POLICY_BLOCK,
                        bucketAclService.checkBucketAcl(user, readAndWriteType, bucket));
            }
            return check;
            /*-------------------验证objectAcl-------------------*/

        }else if(ossApi.target().equals(API_USER)){
            return true;
        }else if(ossApi.target().equals(API_OPEN)){
            return true;
        }else if(ossApi.target().equals(API_MANAGE)){
            return user.getAdmin();
        }else if(ossApi.target().equals(API_OTHER)){
            return ControllerUtils.writeIfReturn(response, ResultCode.UN_KNOW_API,
                    true);
        }
        return false;
    }




    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AuthContext.remove();
        log.debug("-------------------------Request end:"+request.getRequestURI());
    }
}
