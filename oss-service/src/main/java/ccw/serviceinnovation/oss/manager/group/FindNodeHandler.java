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


    public OssGroup find(String etag) {
        return raftClient.find(etag);
    }
}
