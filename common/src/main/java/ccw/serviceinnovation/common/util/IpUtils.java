package ccw.serviceinnovation.common.util;

/**
 * @author 陈翔
 */
public class IpUtils {

    public static String getIp(String addr){
        return addr.substring(0, addr.indexOf(":"));
    }

    public static Integer getPort(String addr){
        return Integer.valueOf(addr.substring(addr.indexOf(":")+1));
    }

}
