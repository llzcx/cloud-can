package ccw.serviceinnovation.ossdata.manager.nacos;

import ccw.serviceinnovation.common.util.IpUtils;
import ccw.serviceinnovation.common.util.http.HttpUtils;
import ccw.serviceinnovation.common.util.net.NetUtil;
import ccw.serviceinnovation.ossdata.constant.OssDataConstant;
import org.springframework.stereotype.Component;


import static ccw.serviceinnovation.ossdata.constant.OssDataConstant.NACOS_SERVER_ADDR;

/**
 * @author 陈翔
 */
@Component
public class TrackerService {
    /**
     * 更新元数据信息
     * @return
     */
    public void updateJraftMeta(){
        try {
            HttpUtils.requestTo("http://"+ NACOS_SERVER_ADDR +
                    "/nacos/v1/ns/instance?serviceName=raft-rpc&ip="+ NetUtil.getIP() +"&port="+
                    IpUtils.getPort(OssDataConstant.RPC_ADDR) +"&metadata=group="+
                    OssDataConstant.GROUP+",port="+OssDataConstant.PORT,"PUT");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
