package ccw.serviceinnovation.oss.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 系统相关常量
 * @author 陈翔
 */
@Component
public class OssApplicationConstant {


    /**
     * 生产环境主机
     */
    public static String LOCATION_PRODUCTION;
    @Value("${myoss.location.production}")
    public void setLocationProduction(String locationProduction) {
        LOCATION_PRODUCTION = locationProduction;
    }

    /**
     * 备份主机
     */
    public static String LOCATION_BACKUPS;
    @Value("${myoss.location.backups}")
    public void setLocationBackups(String locationBackups) {
        LOCATION_BACKUPS = locationBackups;
    }

    /**
     * 归档主机
     */
    public static String LOCATION_PLACE_ON_FILE;
    @Value("${myoss.location.place_on_file}")
    public void setLocationPlaceOnFile(String locationPlaceOnFile) {
        LOCATION_PLACE_ON_FILE = locationPlaceOnFile;
    }

    /**
     *包路径
     */

    public static String PACKAGE;
    @Value("${myoss.package}")
    public void setPACKAGE(String PACKAGE) {
        OssApplicationConstant.PACKAGE = PACKAGE;
    }

    /**
     *接口路径
     */

    public static String CONTROLLER;
    @Value("${myoss.controller}")
    public void setCONTROLLER(String CONTROLLER) {
        OssApplicationConstant.CONTROLLER = CONTROLLER;
    }

    /**
     * 批指令上传接口文档到github
     */
    public static String API_BAT_PATH;
    @Value("${myoss.apiBatPath}")
    public void setApiBatPath(String apiBatPath) {
        OssApplicationConstant.API_BAT_PATH = apiBatPath;
    }

    /**
     * 批指令上传接口文档到github
     */
    public static String ADDRESS;
    @Value("${server.address}")
    public void setAddress(String address) {
        OssApplicationConstant.ADDRESS = address;
    }

    /**
     * 批指令上传接口文档到github
     */
    public static String NACOS_SERVER_ADDR;
    @Value("${spring.cloud.nacos.server-addr}")
    public void setNacosServerAddress(String nacosServerAddress) {
        OssApplicationConstant.NACOS_SERVER_ADDR = nacosServerAddress;
    }

    public static String PORT;
    @Value("${server.port}")
    public void setPort(String port) {
        OssApplicationConstant.PORT = port;
    }


}