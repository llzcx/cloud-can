package ccw.serviceinnovation.ossgateway.gateway;

import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.util.http.HttpUtils;
import ccw.serviceinnovation.ossgateway.manager.redis.NorDuplicateRemovalService;
import ccw.serviceinnovation.ossgateway.mapper.OssObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

/**
 * @author 陈翔
 * @Description 把body中的数据缓存起来
 */
@Slf4j
@Component
public class CacheBodyGlobalFilter implements Ordered, GlobalFilter {

    @Autowired
    private OssObjectMapper ossObjectMapper;

    @Autowired
    NorDuplicateRemovalService norDuplicateRemovalService;



    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();
        String[] pathParams = HttpUtils.getPathParams(uri);
        String path = exchange.getRequest().getPath().toString();
        //下载文件请求格式为/object/download/{objectName}
        if ("object".equals(pathParams[0]) && "download".equals(pathParams[1])) {
            String bucketName = pathParams[2];
            String objectName = pathParams[3];
            addOriginalRequestUrl(exchange, uri);

            OssObject ossObject = ossObjectMapper.selectObjectIdByName(bucketName,objectName);
            String etag = ossObject.getEtag();
            String group = norDuplicateRemovalService.getGroup(etag);
            String newPath = "/object/download/"+ group + "/" + etag + "?name=" + objectName;
            System.out.println("newPath:" + newPath);
            //将请求格式改变重新路由 /object/download/{etag}?name={objectName}
            ServerHttpRequest newRequest = exchange.getRequest().mutate()
                    .path(newPath)
                    .build();
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
            return chain.filter(exchange.mutate()
                    .request(newRequest).build());
        } else {
            System.out.println("no contain");
            // 继续向下执
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }

}