package ccw.serviceinnovation.ossgateway.gateway;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.http.HttpUtils;
import ccw.serviceinnovation.common.util.mybatis.MPUtil;
import ccw.serviceinnovation.oss.manager.nacos.TrackerService;
import ccw.serviceinnovation.ossgateway.OssGatewayApplication;
import ccw.serviceinnovation.ossgateway.gateway.manager.http.Host;
import ccw.serviceinnovation.ossgateway.mapper.OssObjectMapper;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import service.StorageObjectService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

/**
 * @author 陈翔
 */
@Slf4j
@Component
public class CustomChooseRule implements ICustomRule {

    private AtomicInteger atomicInteger = new AtomicInteger(0);


    @DubboReference(version = "1.0.0", group = "object")
    private StorageObjectService storageObjectService;


    private ServiceInstance getServiceInstance(List<ServiceInstance> serviceInstances, String etag) {
        if (storageObjectService == null) {
            synchronized (this) {
                if (storageObjectService == null) {
                    ReferenceConfig<StorageObjectService> reference = new ReferenceConfig<>();
                    reference.setInterface(StorageObjectService.class);
                    reference.setGroup("object");
                    reference.setVersion("1.0.0");
                    reference.setTimeout(3000);
                    reference.setCheck(false);
                    reference.setServices("oss-data-provide");
                    storageObjectService = reference.get();
                    System.out.println("storageObjectService:"+storageObjectService);
                }
            }
        }
        List<Host> allOssDataList = TrackerService.getAllOssDataList();
        for (Host host : allOssDataList) {
            log.info("服务提供者:{}", JSONObject.toJSONString(host));
            UserSpecifiedAddressUtil.setAddress(new Address(host.getIp(), host.getPort(), true));
            LocationVo location = storageObjectService.location(etag);
            if(location!=null){
                String locationAddr = location.getIp() + ":" + location.getPort();
                System.out.println("locationAddr:"+locationAddr);
                for (ServiceInstance serviceInstance : serviceInstances) {
                    String addr = serviceInstance.getHost()+":"+serviceInstance.getPort();
                    System.out.println("serviceInstance:"+addr);
                    if(locationAddr.equals(addr)){
                        return serviceInstance;
                    }else{
                        System.out.println("check error");
                    }
                }
            }else{
                System.out.println("location为空");
            }
        }
        return null;
    }


    @Override
    public ServiceInstance choose(ServerWebExchange exchange, DiscoveryClient discoveryClient) {
        URI originalUrl = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        String path = exchange.getRequest().getPath().toString();
        Map<String, String> stringStringMap = exchange.getRequest().getQueryParams().toSingleValueMap();
        System.out.println("stringStringMap:" + stringStringMap);
        String instancesId = originalUrl.getHost();
        System.out.println("instancesId:"+instancesId);
        Flux<DataBuffer> dataBufferFlux = exchange.getRequest().getBody();
        System.out.println("path:" + path);
        //获取body中的数据
        //String body = FilterRequestResponseUtil.resolveBodyFromRequest(dataBufferFlux);

        //所有服务数据
        List<ServiceInstance> instances = discoveryClient.getInstances(instancesId);
        for (ServiceInstance instance : instances) {
            log.info("{}", instance);
        }
        int index = 0;

        //拦截nacos-provide服务也可拦截服务中特定url
        if ("oss-data-server".equals(instancesId)) {
            if (path.contains("/object/download/")) {
                //下载文件请求
                //根据objectName解析出ossObject 拿到etag
                String objectName = exchange.getRequest().getQueryParams().getFirst("name");
                System.out.println("objectName:"+objectName);
                String etag = HttpUtils.getLastPathParams(path);
                System.out.println("etag:"+etag);
                //根据etag去OssData找
                ServiceInstance serviceInstance = getServiceInstance(instances, etag);
                if(serviceInstance!=null){
                    return serviceInstance;
                }else{
                    throw new OssException(ResultCode.OBJECT_IS_DEFECT);
                }
            } else if (path.contains("/test/demo")) {
                String lastPathParams = HttpUtils.getLastPathParams(path);
                System.out.println("lastPathParams:" + lastPathParams);
            }

        } else {
            /*
             * 别的服务采用轮训
             */
            log.info("other server");
            index = this.getAndIncrement() % instances.size();
        }

        return instances.get(index);
    }


    /**
     * 计算得到当前调用次数
     *
     * @return
     */
    public final int getAndIncrement() {
        int current;
        int next;

        do {
            current = atomicInteger.get();
            next = current >= Integer.MAX_VALUE ? 0 : current + 1;
        } while (!atomicInteger.compareAndSet(current, next));

        return next;
    }

    public static void main(String[] args) {


    }
}