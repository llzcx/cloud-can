package ccw.serviceinnovation.ossgateway.gateway;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.server.ServerWebExchange;

/**
 * @author 陈翔
 */ /*
 * 路由规则
 */
public interface ICustomRule {

    
    ServiceInstance choose(ServerWebExchange exchange, DiscoveryClient discoveryClient);

}