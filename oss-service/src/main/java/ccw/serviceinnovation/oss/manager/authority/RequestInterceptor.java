package ccw.serviceinnovation.oss.manager.authority;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.ControllerUtils;
import ccw.serviceinnovation.oss.manager.authority.bucketacl.BucketAclService;
import ccw.serviceinnovation.oss.manager.authority.bucketpolicy.BucketPolicyService;
import ccw.serviceinnovation.oss.manager.authority.identity.IdentityAuthentication;
import ccw.serviceinnovation.oss.manager.authority.objectacl.ObjectAclService;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
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

import static ccw.serviceinnovation.common.constant.AuthorityConstant.API_BUCKET;
import static ccw.serviceinnovation.common.constant.AuthorityConstant.API_OBJECT;

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
       if(true){
           return true;
       }
        response.setContentType("application/json; charset=utf-8");
        /*-------------------是否为可放行资源-------------------*/
        if(checkCanPassByStatic(request,handler)){
            return true;
        }
        /*-------------------验证令牌-------------------*/
        User user = identityAuthentication.verify(request);
        if(user==null) {
            ControllerUtils.writeReturn(response,ResultCode.TOKEN_ERROR);
            return false;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        OssApi ossApi = method.getAnnotation(OssApi.class);
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        //获取接口的方法名列表
        String[] params = u.getParameterNames(method);
        if(ossApi!=null && params!=null && params.length != 0){
            //目标接口含有ossApi注解,并且接口含有参数
            String type = ossApi.type();
            //判断目标接口是针对桶 / 对象而言的
            if(ossApi.target().equals(API_BUCKET)){
                Bucket bucket = bucketAclService.getBucketFromParam(request, params);
                if(bucket==null) {
                    ControllerUtils.writeReturn(response, ResultCode.BUCKET_IS_DEFECT);
                    return false;
                }
                /*-------------------验证bucketAcl-------------------*/
                return ControllerUtils.writeIfReturn(response, ResultCode.BUCKET_ACL_BLOCK,
                        bucketAclService.checkBucketAcl(user, type, bucket));
            }
            else if(ossApi.target().equals(API_OBJECT)){
                OssObject ossObject = objectAclService.getObjectFromParam(request, params);
                if(ossObject==null){
                    ControllerUtils.writeReturn(response, ResultCode.OBJECT_IS_DEFECT);
                    return false;
                }
                Bucket bucket = bucketMapper.selectById(ossObject.getBucketId());
                if(bucket==null) {
                    ControllerUtils.writeReturn(response, ResultCode.BUCKET_IS_DEFECT);
                    return false;
                }
                /*-------------------判断bucketPolicy-------------------*/
                String accessPath = bucket.getName()+"/"+ossObject.getName();
                Boolean flag = bucketPolicyService.check(user.getId(), bucket.getId(),accessPath,type);
                if(flag != null){
                    return ControllerUtils.writeIfReturn(response, ResultCode.BUCKET_POLICY_BLOCK,flag);
                }
                /*-------------------验证objectAcl-------------------*/
                return ControllerUtils.writeIfReturn(response, ResultCode.OBJECT_ACL_BLOCK,
                        objectAclService.checkObjectAcl(bucket,user, ossApi.type(), ossObject));
            }
        }
        return true;
    }




    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        if(response.getStatus()==500){
//            response.getWriter().println(ApiResponse.fail(ResultCode.ERROR_505));
//        }else if(response.getStatus()==404){
//            response.getWriter().println(ApiResponse.fail(ResultCode.ERROR_404));
//        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }


    public static void main(String[] args) {

    }

}
