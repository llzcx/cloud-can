package ccw.serviceinnovation.node.util;

import ccw.serviceinnovation.node.server.constant.MustParamsKey;

import java.util.*;

public class MainParamsUtil {

    /**
     * 非必要参数
     */
    public static final List<String> notMust =
            Arrays.asList(new String[]{
                    MustParamsKey.ENCRYPT,
            });
    public static Map<String, String> read(String[] array){
        HashMap<String, String> hashMap = new HashMap<String,String>(){
            @Override
            public String get(Object key) {
                String res = super.get(key);
                if(res == null && !notMust.contains(key)){
                    throw new RuntimeException("missing operating parameters:"+key);
                }
                return res;
            }

            @Override
            public String put(String key, String value) {
                key = key.substring(2);
                return super.put(key, value);
            }
        };
        for (String item : array) {
            String[] parts = item.split("=", 2);
            if (parts.length == 2) {
                hashMap.put(parts[0], parts[1]);
            } else {
                // Handle the case where the array element does not contain '='
                // You can choose to ignore or handle it based on your requirement
                System.out.println("Invalid array element: " + item);
            }
        }
        return hashMap;
    }
}
