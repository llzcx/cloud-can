package ccw.serviceinnovation.oss.common;

import ccw.serviceinnovation.oss.manager.authority.api.ApiService;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


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
        Map<String, List<Host>> mp = TrackerService.getAllJraftList(OssApplicationConstant.NACOS_SERVER_ADDR);

    }
}
