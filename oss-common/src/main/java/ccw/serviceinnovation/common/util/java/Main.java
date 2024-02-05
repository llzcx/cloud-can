package ccw.serviceinnovation.common.util.java;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈翔
 */
public class Main {
    public static Map<String,String> getParam(String[] args){
        Map<String,String> mp = new HashMap<>();
        for (String arg : args) {
            String[] split = arg.split("=");
            mp.put(split[0].substring(1),split[1]);
        }
        return mp;
    }

    /**
     * -group=group1
     * -cluster=8021,8022,8023
     * -jraft-data-path=D:\oss\00\jraft_data_pat
     * -position=D:\oss\00\position
     * -server.port=2021
     * @param args
     * {"cluster":"8021,8022,8023","server.port":"2021",
     * "jraft-data-path":"D:\\oss\\00\\jraft_data_pat",
     * "position":"D:\\oss\\00\\position","group":"group1"}
     */
    public static void main(String[] args) {
        System.out.println(JSONObject.toJSONString(getParam(args)));
    }
}
