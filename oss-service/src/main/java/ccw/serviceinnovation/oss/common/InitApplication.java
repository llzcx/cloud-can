package ccw.serviceinnovation.oss.common;

import org.apache.rocketmq.remoting.RPCHook;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.raft.rpc.DataGrpcHelper;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 初始化方法
 *
 * @author 陈翔
 */
@Component
public class InitApplication {

    public static DefaultMQProducer producer;


    /**
     * 在容器初始化之前执行
     */
    public static void beforeSpring() {

    }


    @Autowired
    ConsistentHashing consistentHashing;


    /**
     * 在容器初始化之后执行
     */
    public void afterSpring() throws Exception {
        DataGrpcHelper.initGRpc();
        Map<String, List<Host>> mp = TrackerService.getAllJraftList(OssApplicationConstant.NACOS_SERVER_ADDR);
        System.out.println("一致性hash初始化:");
        for (Map.Entry<String, List<Host>> stringListEntry : mp.entrySet()) {
            ConsistentHashing.physicalNodes.add(stringListEntry.getKey());
            System.out.println("添加:" + stringListEntry.getKey());
        }
        for (String nodeIp : ConsistentHashing.physicalNodes) {
            consistentHashing.addPhysicalNode(nodeIp);
        }

        //创建一个消息生产者，传入的是消息组名称
        producer = new DefaultMQProducer("oss-group");
        //输入nameserver服务的地址
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.setInstanceName("cold-producer");
        //启动生产者
        producer.start();
        System.out.println("producer started.");
        producer.shutdown();


    }
}
