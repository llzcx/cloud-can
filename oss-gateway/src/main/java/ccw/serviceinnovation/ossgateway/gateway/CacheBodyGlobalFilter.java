package ccw.serviceinnovation.ossgateway.gateway;

import ccw.serviceinnovation.common.constant.ObjectStateConstant;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.http.HttpUtils;

import ccw.serviceinnovation.ossgateway.manager.redis.NorDuplicateRemovalService;
import ccw.serviceinnovation.ossgateway.manager.redis.ObjectStateRedisService;
import ccw.serviceinnovation.ossgateway.mapper.OssObjectMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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


    @Autowired
    ObjectStateRedisService objectStateRedisService;

    public String getObjectName(String[] pathParams){
        StringBuilder sb = new StringBuilder();
        int i = 3;
        for (; i < pathParams.length; i++) {
            sb.append(pathParams[i]);
        }
        return sb.toString();
    }

    public ServerHttpResponseDecorator handle(ServerWebExchange exchange){
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {

                String contentType = getDelegate().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
                Boolean flag = MediaType.APPLICATION_JSON_VALUE.equals(contentType) || MediaType.APPLICATION_JSON_UTF8_VALUE.equals(contentType);
                if (body instanceof Flux && flag) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffer -> {
                        //DefaultDataBufferFactory join 乱码的问题解决
                        DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                        DataBuffer join = dataBufferFactory.join(dataBuffer);
                        // probably should reuse buffers
                        byte[] content = new byte[join.readableByteCount()];
                        join.read(content);
                        //释放掉内存
                        DataBufferUtils.release(join);
                        String result = new String(content, StandardCharsets.UTF_8);
                        byte[] uppedContent = new String(result.getBytes(), StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
                        return bufferFactory.wrap(uppedContent);
                    }));
                }
                return super.writeWith(body);
            }
        };
        return decoratedResponse;
    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI uri = exchange.getRequest().getURI();
        String[] pathParams = HttpUtils.getPathParams(uri);
        String path = exchange.getRequest().getPath().toString();
        //下载文件请求格式为/object/download/{objectName}
        if ("object".equals(pathParams[0])) {
            String bucketName = pathParams[2];
            String objectName = getObjectName(pathParams);
            addOriginalRequestUrl(exchange, uri);
            System.out.println(bucketName+"/"+objectName);
            OssObject ossObject = ossObjectMapper.selectObjectIdByName(bucketName,objectName);
            log.info(JSONObject.toJSONString(ossObject));
            String etag = ossObject.getEtag();
            String group = norDuplicateRemovalService.getGroup(etag);
            Integer state = objectStateRedisService.getState(bucketName, objectName);
            if(ObjectStateConstant.FREEZING.equals(state)){
                throw new OssException(ResultCode.OBJECT_STATE_EXCEPTION);
            }else if(ObjectStateConstant.UNFREEZING.equals(state)){
                throw new OssException(ResultCode.OBJECT_STATE_EXCEPTION);
            }else{
                String newPath = "/object/"+pathParams[1]+"/"+ group + "/" + etag + "?name=" + objectName;
                if(ossObject.getSecret()!=null){
                    newPath += "&secret="+ossObject.getSecret();
                }
                System.out.println("newPath:" + newPath);
                //将请求格式改变重新路由 /object/download/{etag}?name={objectName}
                ServerHttpRequest newRequest = exchange.getRequest().mutate()
                        .path(newPath)

                        .build();
                exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, newRequest.getURI());
                return chain.filter(exchange.mutate()
//                        .response(handle(exchange))
                        .request(newRequest).build());
            }

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