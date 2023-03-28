package ccw.serviceinnovation.common.util.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * @author 陈翔
 */
public class NetUtil {
    public static InetAddress getLANAddressOnWindows() {
        try {
            Enumeration<NetworkInterface> nifs = NetworkInterface.getNetworkInterfaces();
            while (nifs.hasMoreElements()) {
                NetworkInterface nif = nifs.nextElement();

                if (nif.getName().startsWith("wlan")) {
                    Enumeration<InetAddress> addresses = nif.getInetAddresses();

                    while (addresses.hasMoreElements()) {

                        InetAddress addr = addresses.nextElement();
                        if (addr.getAddress().length == 4) { // 速度快于 instanceof
                            return addr;
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace(System.err);
        }
        return null;
    }

    public static String getIP(){
        return "192.168.50.236";
    }
}
