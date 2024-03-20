package ccw.serviceinnovation.oss.config;

import ccw.serviceinnovation.hash.directcalculator.EtagDirectCalculator;
import ccw.serviceinnovation.hash.directcalculator.MD5EtagDirectCalculatorAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtagDirectCalculatorConfig {
    @Bean
    public EtagDirectCalculator checkSumHandler(){
        return new MD5EtagDirectCalculatorAdapter();
    }
}
