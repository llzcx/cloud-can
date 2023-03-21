package ccw.serviceinnovation.oss.manager.nacos;

import ccw.serviceinnovation.common.util.http.HttpUtils;
import ccw.serviceinnovation.ossgateway.gateway.manager.http.Host;
import ccw.serviceinnovation.ossgateway.gateway.manager.http.Response;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.List;

import static ccw.serviceinnovation.ossgateway.constant.GateWayConstant.NACOS_SERVER_ADDR;


/**
 * @author 陈翔
 */
public class TrackerService {
    /**
     * 获取存储服务列表
     * @return
     */
    public static List<Host> getAllOssDataList(){
        try {
            System.out.println("nacos-addr:"+NACOS_SERVER_ADDR);
            String response = HttpUtils.request("http://"+NACOS_SERVER_ADDR + "/nacos/v1/ns/instance/list?serviceName=oss-data-provide");
            return  JSONObject.parseObject(response, Response.class).getHosts();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
