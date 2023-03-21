package ccw.serviceinnovation.oss;

import ccw.serviceinnovation.oss.common.InitApplication;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * @author 陈翔
 */
@SpringBootApplication(scanBasePackages={"ccw.serviceinnovation.oss.*"})
@MapperScan(basePackages = "ccw.serviceinnovation.oss.mapper")
@EnableTransactionManagement
@EnableDiscoveryClient
@EnableDubbo
public class OssApplication {

    public static ConfigurableApplicationContext run;
    public static void main(String[] args) {
        InitApplication.beforeSpring();
        run = SpringApplication.run(OssApplication.class, args);
        InitApplication initApplication =  run.getBean(InitApplication.class);
        initApplication.afterSpring();
    }
}
