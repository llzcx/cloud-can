package ccw.serviceinnovation.oss.config;

import ccw.serviceinnvation.nodeclient.RaftClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NodeClient {
    @Bean
    public RaftClient testBean() {
        return new RaftClient();
    }
}
