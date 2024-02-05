package ccw.serviceinnovation.oss;

import ccw.serviceinnovation.oss.common.InitApplication;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import service.raft.client.RaftRpcRequest;

/**
 * 启动类
 * @author 陈翔
 */
@SpringBootApplication(scanBasePackages={"ccw.serviceinnovation.oss.*"})
@MapperScan(basePackages = "ccw.serviceinnovation.oss.mapper")
@EnableTransactionManagement
@EnableDiscoveryClient
public class OssApplication {
    public static ConfigurableApplicationContext run;
    public static void main(String[] args) throws Exception{
        InitApplication.beforeSpring();
        run = SpringApplication.run(OssApplication.class, args);
        InitApplication initApplication =  run.getBean(InitApplication.class);
        initApplication.afterSpring();
        RaftRpcRequest.init(OssApplicationConstant.NACOS_SERVER_ADDR);
    }
}
