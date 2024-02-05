package ccw.serviceinnovation.common.util;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * @author 陈翔
 */
public class IpUtils {


    public static String getAddr(String ip,String port){
        return ip + ":" + port;
    }

    public static String getAddr(String ip,Integer port){
        return ip + ":" + port;
    }
    public static String getIp(String addr){
        return addr.substring(0, addr.indexOf(":"));
    }

    public static Integer getPort(String addr){
        return Integer.valueOf(addr.substring(addr.indexOf(":")+1));
    }

    /**
     * 根据输入端口号，递增递归查询可使用端口
     * @param port  端口号
     * @return  如果被占用，递归；否则返回可使用port
     */

    public static int findAvailablePort() {
        int port = 0; // 默认为0，表示系统将为你选择一个可用的端口号
        try (ServerSocket socket = new ServerSocket(0)) {
            port = socket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }

}
