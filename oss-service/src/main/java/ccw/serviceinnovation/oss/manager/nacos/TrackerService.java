package ccw.serviceinnovation.oss.manager.nacos;

import ccw.serviceinnovation.common.util.http.HttpUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ccw.serviceinnovation.oss.constant.OssApplicationConstant.NACOS_SERVER_ADDR;

/**
 * @author 陈翔
 */
@Component
public class TrackerService {


    /**
     * 获取存储服务列表
     * @return
     */
    public List<Host> getAllOssDataList(){
        try {
            String response = HttpUtils.request("http://"+NACOS_SERVER_ADDR + "/nacos/v1/ns/instance/list?serviceName=oss-data-provide");
            return  JSONObject.parseObject(response, Response.class).getHosts();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Jraft服务列表
     * @return
     */
    public Map<String,List<Host>> getAllJraftList(){
        try {
            String response = HttpUtils.request("http://"+NACOS_SERVER_ADDR + "/nacos/v1/ns/instance/list?serviceName=raft-rpc");
            List<Host> hosts = JSONObject.parseObject(response, Response.class).getHosts();
            Map<String,List<Host>> newHosts = new HashMap<>();
            for (Host host : hosts) {
                String group = host.getMetadata().getGroup();
                List<Host> list = newHosts.get(group);
                if(list==null){
                    list = new ArrayList<>();
                }
                list.add(host);
                newHosts.put(group,list);
                System.out.println("put了:"+group+":"+JSONObject.toJSONString(list));
            }
            return  newHosts;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

}
