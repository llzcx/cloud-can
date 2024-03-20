package ccw.serviceinnovation.oss.config;

import ccw.serviceinnovation.hash.checksum.EtagHandler;
import ccw.serviceinnovation.hash.checksum.Crc32EtagHandlerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtagCheckSumConfig {

    @Bean
    public EtagHandler checkSumHandler(){
        return new Crc32EtagHandlerAdapter();
    }
}
