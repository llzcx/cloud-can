package ccw.serviceinnovation;

import ccw.serviceinnovation.osscolddata.manager.init.Init;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;


/**
 * @author 陈翔
 */
@SpringBootApplication(scanBasePackages={"ccw.serviceinnovation.osscolddata.*"})
public class OssColdDataApplication {
    public static void main(String[] args) throws Exception{
        BufferedReader bufferedReader = new BufferedReader(new FileReader("123"));
        ConfigurableApplicationContext cac = SpringApplication.run(OssColdDataApplication.class, args);
        Init.fileInit();
        Init.initFileKey();
    }
}
