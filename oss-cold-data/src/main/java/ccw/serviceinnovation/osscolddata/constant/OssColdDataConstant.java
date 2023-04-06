package ccw.serviceinnovation.osscolddata.constant;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * oss相关常量
 * @author 陈翔
 */
@Component
public class OssColdDataConstant {

    /**
     * 存储位置
     */
    public static String POSITION;
    @Value("${position}")
    public void setPRODUCTION(String location) {
        OssColdDataConstant.POSITION = location;
    }

    public static String ADDRESS;
    @Value("${server.address}")
    public void setADDRESS(String address) {
        OssColdDataConstant.ADDRESS = address;
    }

    public static String PORT;
    @Value("${server.port}")
    public void setPORT(String port) {
        OssColdDataConstant.PORT = port;
    }

    public static String COLD_STORAGE_NAME;
    @Value("${spring.cloud.nacos.discovery.metadata.cold-storage-name}")
    public void setGROUP(String coldStorageName) {
        OssColdDataConstant.COLD_STORAGE_NAME = coldStorageName;
    }

    public static String NACOS_SERVER_ADDR;
    @Value("${spring.cloud.nacos.server-addr}")
    public void setNacosServerAddress(String nacosServerAddress) {
        OssColdDataConstant.NACOS_SERVER_ADDR = nacosServerAddress;
    }



}