package ccw.serviceinnovation.ossdata;

import ccw.serviceinnovation.ossdata.manager.init.Init;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author 陈翔
 */
@SpringBootApplication(scanBasePackages={"ccw.serviceinnovation.ossdata.*"})
@EnableDubbo
public class OssDataApplication {
    public static void main(String[] args) throws Exception{
        ConfigurableApplicationContext cac = SpringApplication.run(OssDataApplication.class, args);
        Init bean = cac.getBean(Init.class);
//        bean.fileInit();
        bean.initFileKey();
        bean.initJraft();
        bean.registerNacos();

    }
}
