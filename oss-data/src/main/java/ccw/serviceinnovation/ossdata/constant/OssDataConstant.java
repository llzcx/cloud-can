package ccw.serviceinnovation.ossdata.constant;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * oss相关常量
 * @author 陈翔
 */
@Component
public class OssDataConstant {

    /**
     * 存储位置
     */
    public static String POSITION;
    @Value("${position}")
    public void setPRODUCTION(String location) {
        OssDataConstant.POSITION = location;
    }

    public static String ADDRESS;
    @Value("${server.address}")
    public void setADDRESS(String address) {
        OssDataConstant.ADDRESS = address;
    }

    public static String PORT;
    @Value("${server.port}")
    public void setPORT(String port) {
        OssDataConstant.PORT = port;
    }

    public static String GROUP;
    @Value("${group}")
    public void setGROUP(String volume) {
        OssDataConstant.GROUP = volume;
    }

    public static String CLUSTER;
    @Value("${cluster}")
    public void setCLUSTER(String cluster) {
        OssDataConstant.CLUSTER = cluster;
    }

    public static String JRAFT_DATA_PATH;
    @Value("${jraft-data-path}")
    public void setJRAFT_DATA_PATH(String jraft_data_path) {
        OssDataConstant.JRAFT_DATA_PATH = jraft_data_path;
    }

    public static String RPC_ADDR;
    @Value("${rpc-addr}")
    public void setRPC_ADDR(String rpc_addr) {
        OssDataConstant.RPC_ADDR = rpc_addr;
    }
    public static String NACOS_SERVER_ADDR;
    @Value("${spring.cloud.nacos.server-addr}")
    public void setNacosServerAddress(String nacosServerAddress) {
        OssDataConstant.NACOS_SERVER_ADDR = nacosServerAddress;
    }


    public static String PROVIDE_PORT;
    @Value("${dubbo.protocol.port}")
    public void setPROVIDE_PORT(String PROVIDE_PORT) {
        OssDataConstant.PROVIDE_PORT = PROVIDE_PORT;
    }

}