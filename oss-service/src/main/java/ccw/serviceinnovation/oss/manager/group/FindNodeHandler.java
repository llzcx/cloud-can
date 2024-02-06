package ccw.serviceinnovation.oss.manager.group;

import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.loadbalance.*;
import ccw.serviceinnvation.nodeclient.RaftClient;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class FindNodeHandler {

    @Autowired
    RaftClient raftClient;

    LoadBalancer loadBalancer = new ConsistentHashLoadBalancer();

    public OssGroup find(String etag) {
        List<OssGroup> list = raftClient.getList();
        if (list.size() == 0) {
            throw new OssException(ResultCode.SERVER_EXCEPTION);
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            List<Server> serverList = list.stream().map(ossGroup -> (Server) ossGroup)
                    .collect(Collectors.toList());
            return (OssGroup) loadBalancer.select(serverList, new Invocation(etag));
        }
    }
}
