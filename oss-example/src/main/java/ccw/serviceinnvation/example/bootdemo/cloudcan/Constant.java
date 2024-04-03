package ccw.serviceinnvation.example.bootdemo.cloudcan;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constant {

    public static String BUCKET;

    public static String RES_OK = "ok";

    public static String RES_ERROR = "error";

    @Autowired
    public Constant(@Value("${cloud-can.bucket}") String bucket) {
        BUCKET = bucket;
    }
}
