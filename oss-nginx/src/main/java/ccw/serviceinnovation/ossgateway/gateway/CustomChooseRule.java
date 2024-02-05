package ccw.serviceinnovation.ossgateway.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 陈翔
 */
@Slf4j
@Component
public class CustomChooseRule implements ICustomRule {

    private AtomicInteger atomicInteger = new AtomicInteger(0);


    private ServiceInstance getServiceInstance(List<ServiceInstance> serviceInstances, String group,String etag) throws Exception{
//        Map<String, List<Host>> allJraftList = TrackerService.getAllJraftList(GateWayConstant.NACOS_SERVER_ADDR);
//        List<Host> hosts = allJraftList.get(group);
//        if(hosts!=null){
//            for (Host host : hosts) {
//                RaftRpcRequest.RaftRpcRequestBo leader = RaftRpcRequest.getLeader(GateWayConstant.NACOS_SERVER_ADDR, group);
//                LocationVo locationVo = RaftRpcRequest.get(leader.getCliClientService(), leader.getPeerId(), etag);
//                log.info("{}的group:{}(leader:{})的定位:{}",etag, group,leader.getPeerId(),JSONObject.toJSONString(locationVo));
//               if(locationVo!=null){
//                   //拿到了HTTP(springboot服务的ip和端口号)
//                   Integer port = host.getMetadata().getPort();
//                   for (ServiceInstance serviceInstance : serviceInstances) {
//                       log.info("比较{}和{}",serviceInstance.getHost()+":" +serviceInstance.getPort(),JSONObject.toJSONString(host));
//                       //HTTP服务和RPC服务的IP必然相同 端口号不同
//                       //这里要拿HTTP的端口号比较
//                       if(serviceInstance.getPort()==port){
//                           return serviceInstance;
//                       }
//                   }
//                   //数据丢失
//                   throw new OssException(ResultCode.DATA_NOT_FOUND);
//               }else{
//                   //文件不存在
//                   throw new OssException(ResultCode.OBJECT_IS_DEFECT);
//               }
//            }
//        }else{
//            //数据服务器异常
//            throw new OssException(ResultCode.SERVER_EXCEPTION);
//        }
        return null;
    }


    @Override
    public ServiceInstance choose(ServerWebExchange exchange, DiscoveryClient discoveryClient) throws Exception {
//        URI originalUrl = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
//        String path = exchange.getRequest().getPath().toString();
//        URI uri = exchange.getRequest().getURI();
//        String[] pathParams = HttpUtils.getPathParams(uri);
////        Map<String, String> stringStringMap = exchange.getRequest().getQueryParams().toSingleValueMap();
//        String instancesId = originalUrl.getHost();
////        Flux<DataBuffer> dataBufferFlux = exchange.getRequest().getBody();
//        log.info("path:{}",path);
//        //获取body中的数据
//        //String body = FilterRequestResponseUtil.resolveBodyFromRequest(dataBufferFlux);
//
//        //所有服务数据(Springboot的下载服务)
//        List<ServiceInstance> instances = discoveryClient.getInstances(instancesId);
//        for (ServiceInstance instance : instances) {
//            log.info("{}", instance);
//        }
//        int index = 0;
//
//        //拦截nacos-provide服务也可拦截服务中特定url
//        if ("oss-data-server".equals(instancesId)) {
//            String way = pathParams[1];
//            if ("object".equals(pathParams[0])) {
//                //获取group和etag
//                String group = pathParams[2];
//                String etag = pathParams[3];
//                log.info("group:{},etag:{}",group,etag);
//                //根据etag去OssData找
//                ServiceInstance serviceInstance = getServiceInstance(instances,group, etag);
//                if(serviceInstance!=null){
//                    return serviceInstance;
//                }else{
//                    throw new OssException(ResultCode.OBJECT_IS_DEFECT);
//                }
//            } else if (path.contains("/test/demo")) {
//                String lastPathParams = HttpUtils.getLastPathParams(path);
//                System.out.println("lastPathParams:" + lastPathParams);
//            }
//        } else {
//            /*
//             * 别的服务采用轮训
//             */
//            log.info("other server");
//            index = this.getAndIncrement() % instances.size();
//        }
//
//        return instances.get(index);
        return null;
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