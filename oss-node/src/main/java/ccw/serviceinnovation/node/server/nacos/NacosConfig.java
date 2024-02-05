package ccw.serviceinnovation.node.server.nacos;

import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;

public class NacosConfig {
    public NamingService namingService;

    public NacosConfig() {

    }

    public void connect() throws NacosException {
        namingService = NamingFactory.createNamingService(RegisterConstant.NACOS_ADDR);
        String ip = RegisterConstant.HOST;
        Integer port = RegisterConstant.PORT;
        Instance instance = new Instance();
        instance.setIp(ip);
        instance.setInstanceId(RegisterConstant.GROUP_NAME + RegisterConstant.PORT);
        instance.setPort(port);
        instance.setClusterName(RegisterConstant.GROUP_NAME);
        namingService.registerInstance("oss","raft" ,instance);
        System.out.println("nacos connect success.");
    }

}
