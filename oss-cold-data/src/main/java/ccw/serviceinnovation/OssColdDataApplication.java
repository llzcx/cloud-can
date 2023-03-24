package ccw.serviceinnovation;

import ccw.serviceinnovation.osscolddata.manager.init.Init;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author 陈翔
 */
public class OssColdDataApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext cac = SpringApplication.run(OssColdDataApplication.class, args);
        Init.fileInit();
        Init.initFileKey();
        Init.initPRC();
    }
}
