package ccw.serviceinnovation.ossgateway.gateway.manager.init;

import org.springframework.stereotype.Component;
import service.raft.rpc.DataGrpcHelper;

/**
 * 初始化操作
 * @author 陈翔
 */
@Component
public class Init {


    public static void beforeBeanInitialize(){

    }


    public static void afterBeanInitialize(){
        DataGrpcHelper.initGRpc();
    }

}

