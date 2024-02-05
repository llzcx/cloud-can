package ccw.serviceinnovation.ossgateway.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author 陈翔
 */
@Configuration
public class GateWayConstant {
    /**
     * 批指令上传接口文档到github
     */
    public static String NACOS_SERVER_ADDR;
    @Value("${spring.cloud.nacos.server-addr}")
    public void setNacosServerAddress(String nacosServerAddress) {
        GateWayConstant.NACOS_SERVER_ADDR = nacosServerAddress;
    }

}
