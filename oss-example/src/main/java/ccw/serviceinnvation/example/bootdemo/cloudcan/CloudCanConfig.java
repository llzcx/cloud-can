package ccw.serviceinnvation.example.bootdemo.cloudcan;

import ccw.serviceinnvation.sdk.CloudCan;
import ccw.serviceinnvation.sdk.CloudCanClient;
import ccw.serviceinnvation.sdk.CloudCanClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudCanConfig {

    @Value("${cloud-can.endpoint}")
    private String endpoint;

    @Value("${cloud-can.username}")
    private String username;

    @Value("${cloud-can.password}")
    private String password;

    @Bean
    public CloudCan cloudCan() {
//        return new CloudCanClientBuilder()
//                .build(endpoint, username, password);
        return null;
    }
}
