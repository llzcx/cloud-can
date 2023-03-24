package ccw.serviceinnovation.common.nacos;

import ccw.serviceinnovation.common.util.http.HttpUtils;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 陈翔
 */
public class TrackerService {


    /**
     * 获取存储服务列表
     * @return
     */
    public static List<Host> getAllOssDataList(String nacosPath){
        try {
            String response = HttpUtils.request("http://"+nacosPath + "/nacos/v1/ns/instance/list?serviceName=oss-data-provide");
            return  JSONObject.parseObject(response, Response.class).getHosts();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 获取Jraft服务列表(GRPC)
     * @return
     */
    public static Map<String,List<Host>> getAllJraftList(String nacosPath){
        try {
            String response = HttpUtils.request("http://"+nacosPath + "/nacos/v1/ns/instance/list?serviceName=raft-rpc");
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

    /**
     * 获取归档服务列表(HTTP)
     * @return
     */
    public static List<Host> getColdList(String nacosPath){
        try {
            String response = HttpUtils.request("http://"+nacosPath + "/nacos/v1/ns/instance/list?serviceName=oss-cold-data");
            List<Host> hosts = JSONObject.parseObject(response, Response.class).getHosts();
            return hosts;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param nacosPath
     * @param name 归档存储名
     * @return
     */
    public static Host getCold(String nacosPath,String name){
        List<Host> coldList = getColdList(nacosPath);
        if(coldList==null){
            return null;
        }
        for (Host host : coldList) {
            String name1 = host.getMetadata().getCold_storage_name();
            if(name1.equals(name)){
                return host;
            }
        }
        return null;
    }

}
