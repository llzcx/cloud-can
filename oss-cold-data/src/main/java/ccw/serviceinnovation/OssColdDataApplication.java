package ccw.serviceinnovation;

import ccw.serviceinnovation.osscolddata.manager.init.Init;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author 陈翔
 */
@SpringBootApplication(scanBasePackages={"ccw.serviceinnovation.osscolddata.*"})
public class OssColdDataApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext cac = SpringApplication.run(OssColdDataApplication.class, args);
        Init.fileInit();
        Init.initFileKey();
    }
}
