package ccw.serviceinnvation.example.bootdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 启动类
 * @author 陈翔
 */
@SpringBootApplication(scanBasePackages={"ccw.serviceinnvation.example.bootdemo"})
public class DemoApplication {
    public static ConfigurableApplicationContext run;
    public static void main(String[] args) {
        run = SpringApplication.run(DemoApplication.class, args);
    }
}
