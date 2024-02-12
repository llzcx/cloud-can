package ccw.serviceinnovation.node.server.nacos;

import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

@Slf4j
public class NacosConfig {
    public NamingService namingService;

    public NacosConfig() {

    }

    public void connect() throws NacosException {
        namingService = NamingFactory.createNamingService("127.0.0.1:8848");
        String ip = RegisterConstant.HOST;
        Integer port = RegisterConstant.PORT;
        Instance instance = new Instance();
        instance.setIp(ip);
        instance.setInstanceId(RegisterConstant.GROUP_NAME + RegisterConstant.HOST + RegisterConstant.PORT);
        instance.setPort(port);
        instance.setClusterName(RegisterConstant.GROUP_NAME);
        namingService.registerInstance("oss", "raft", instance);
        log.debug("nacos connect success.");
    }

    public static void main(String[] args) throws NacosException, IOException, InterruptedException {
        NamingService namingService1 = NamingFactory.createNamingService("127.0.0.1:8848");
        System.out.println(namingService1.getServerStatus());
        namingService1.registerInstance("oss", "11.11.11.11", 8080, "beijing");
        Thread.sleep(10000);

    }


}
