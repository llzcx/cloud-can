package ccw.serviceinnvation.example.bootdemo.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
 
 
@Data
@Component
@ConfigurationProperties(prefix = "s3")
public class StorageProperty {
    private String url;
    private String accessKey;
    private String secretKey;
    private String bucketName;
//    private long callTimeOut = 60000;
//    private long readTimeOut = 300000;
}