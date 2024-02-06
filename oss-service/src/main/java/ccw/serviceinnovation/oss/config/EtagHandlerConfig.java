package ccw.serviceinnovation.oss.config;

import ccw.serviceinnovation.hash.etag.Crc32EtagHandlerAdapter;
import ccw.serviceinnovation.hash.etag.EtagHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class EtagHandlerConfig {
    @Bean
    public EtagHandler testBean() {
        return new Crc32EtagHandlerAdapter();
    }
}
