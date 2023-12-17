package ccw.serviceinnovation.oss.logger;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
/**
 * 自定义拦截器，拦截请求路径,打印请求日志
 * @Author: 陈翔
 */
@Component
@Slf4j
public class LoggerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.info("--------------- request is begin! --------------- URL : " + request.getRequestURI());
//        log.info(request.getRequestURI());
        return true;

    }
 
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        log.info("--------------- request is end! ---------------");
        if (ex != null) {
            log.error("error info: ", ex);
        }
    }
}