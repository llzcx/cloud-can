package ccw.serviceinnovation.ossgateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author 陈翔
 */
@MapperScan("ccw.serviceinnovation.ossgateway.mapper")
@SpringBootApplication(scanBasePackages={"ccw.serviceinnovation.ossgateway.*"})
public class OssGatewayApplication {

    public static ConfigurableApplicationContext cac;

    public static void main(String[] args) {
        cac = SpringApplication.run(OssGatewayApplication.class, args);
    }

    public static <T> T getBean(Class<T> tClass){
        return cac.getBean(tClass);
    }
}
