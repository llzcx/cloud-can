package ccw.serviceinnovation.oss.common;


import ccw.serviceinnovation.oss.constant.OssApplicationConstant;
import ccw.serviceinnvation.nodeclient.RaftClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * 初始化方法
 *
 * @author 陈翔
 */
@Component
public class InitApplication {


    @Autowired
    RaftClient raftClient;

    /**
     * 在容器初始化之前执行
     */
    public static void beforeSpring() {

    }

    /**
     * 在容器初始化之后执行
     */
    public void afterSpring() throws Exception {
        //客户端订阅Nacos
        raftClient.listenChange(OssApplicationConstant.NACOS_SERVER_ADDR);
    }
}
