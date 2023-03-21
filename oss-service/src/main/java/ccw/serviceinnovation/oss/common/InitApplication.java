package ccw.serviceinnovation.oss.common;

import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import ccw.serviceinnovation.oss.manager.nacos.Host;
import ccw.serviceinnovation.oss.manager.nacos.TrackerService;
import com.alipay.sofa.jraft.example.ne.NeGrpcHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 初始化方法
 * @author 陈翔
 */
@Component
public class InitApplication {


    /**
     * 在容器初始化之前执行
     */
    public static void beforeSpring(){

    }
    
    @Autowired
    TrackerService trackerService;

    @Autowired
    ConsistentHashing consistentHashing;


    /**
     * 在容器初始化之后执行
     */
    public void afterSpring(){
        //初始化
//        List<Host> allOssDataList = trackerService.getAllOssDataList();
//        for (Host host : allOssDataList) {
//            //一致性hash
//            ConsistentHashing.physicalNodes.add(host.getIp()+":"+host.getPort());
//        }
        NeGrpcHelper.initGRpc();
        Map<String, List<Host>> mp = trackerService.getAllJraftList();
        System.out.println("一致性hash初始化:");
        for (Map.Entry<String, List<Host>> stringListEntry : mp.entrySet()) {
            ConsistentHashing.physicalNodes.add(stringListEntry.getKey());
            System.out.println("添加:"+stringListEntry.getKey());
        }
        for (String nodeIp : ConsistentHashing.physicalNodes) {
            consistentHashing.addPhysicalNode(nodeIp);
        }
    }
}
