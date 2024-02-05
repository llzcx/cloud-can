package ccw.serviceinnovation.ossgateway.config;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CorsWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestOrigin = exchange.getRequest().getHeaders().getOrigin();
        if (requestOrigin != null) {
            ServerHttpResponse response = exchange.getResponse();
            response.getHeaders().set("Access-Control-Allow-Origin", requestOrigin);
            response.getHeaders().set("Access-Control-Allow-Methods", "*");
            response.getHeaders().set("Access-Control-Allow-Headers", "*");
            response.getHeaders().set("Access-Control-Allow-Credentials", "true");
            if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return Mono.empty();
            }
        }
        return chain.filter(exchange);
    }
}