package ccw.serviceinnovation.oss.config;

import ccw.serviceinnovation.oss.logger.LoggerInterceptor;
import ccw.serviceinnovation.oss.manager.authority.RequestInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
 
/**
 * 拦截器配置类
 * http://localhost:8080/swagger-ui/index.html
 * @Author: 陈翔
 */
@Configuration
public class RequestConfig implements WebMvcConfigurer {
 
    @Resource
    private LoggerInterceptor loggerInterceptor;

    @Resource
    private RequestInterceptor requestInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器，配置拦截地址
        registry.addInterceptor(loggerInterceptor)
                .addPathPatterns("/**")
                .order(1)
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");

        registry.addInterceptor(requestInterceptor)
                .order(2);
    }



}