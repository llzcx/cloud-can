package ccw.serviceinnovation.oss.common;

import ccw.serviceinnovation.oss.manager.authority.api.ApiService;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import service.raft.rpc.DataGrpcHelper;

import java.util.List;
import java.util.Map;


import static ccw.serviceinnovation.oss.constant.OssApplicationConstant.MQ_ADDR;
import static ccw.serviceinnovation.oss.manager.mq.ColdConsumer.MQ_FREEZE_GROUP;

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

    @Autowired
    ColdConsistHashing coldConsistHashing;

    @Autowired
    ApiService apiService;


    /**
     * 在容器初始化之后执行
     */
    public void afterSpring() throws Exception {
//        apiService.initApi();
        DataGrpcHelper.initGRpc();
        Map<String, List<Host>> mp = TrackerService.getAllJraftList(OssApplicationConstant.NACOS_SERVER_ADDR);
        System.out.println("group 一致性hash初始化:");
        for (Map.Entry<String, List<Host>> stringListEntry : mp.entrySet()) {
            ConsistentHashing.physicalNodes.add(stringListEntry.getKey());
            System.out.println("添加:" + stringListEntry.getKey());
        }
        for (String nodeIp : ConsistentHashing.physicalNodes) {
            consistentHashing.addPhysicalNode(nodeIp);
        }
        List<Host> coldList = TrackerService.getColdList(OssApplicationConstant.NACOS_SERVER_ADDR);
        System.out.println("cold_storage_name 一致性hash初始化:");
        for (Host host : coldList) {
            ColdConsistHashing.physicalNodes.add(host.getMetadata().cold_storage_name);
            System.out.println("添加:" + host.getMetadata().cold_storage_name);
        }
        for (String nodeIp : ColdConsistHashing.physicalNodes) {
            coldConsistHashing.addPhysicalNode(nodeIp);
        }

        //创建一个消息生产者，传入的是消息组名称
        producer = new DefaultMQProducer(MQ_FREEZE_GROUP);
        //输入nameserver服务的地址
        producer.setNamesrvAddr(MQ_ADDR);
        producer.setInstanceName("cold-producer");
        //启动生产者
        producer.start();
        System.out.println("producer started.");


    }
}
