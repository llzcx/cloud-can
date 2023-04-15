package ccw.serviceinnovation.ossgateway;

import ccw.serviceinnovation.ossgateway.constant.GateWayConstant;
import ccw.serviceinnovation.ossgateway.gateway.manager.init.Init;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import service.raft.client.RaftRpcRequest;

/**
 * @author 陈翔
 */
@MapperScan("ccw.serviceinnovation.ossgateway.mapper")
@SpringBootApplication(scanBasePackages={"ccw.serviceinnovation.ossgateway.*"})
public class OssGatewayApplication {

    public static ConfigurableApplicationContext cac;

    public static void main(String[] args) {
        cac = SpringApplication.run(OssGatewayApplication.class, args);
        Init.afterBeanInitialize();
        RaftRpcRequest.init(GateWayConstant.NACOS_SERVER_ADDR);
    }

    public static <T> T getBean(Class<T> tClass){
        return cac.getBean(tClass);
    }
}
