package ccw.serviceinnovation.node.server.db.processor;


import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.ReadDelEventRequest;
import service.raft.request.ReadEventRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author 陈翔
 */
public class ReadEventRequestProcessor implements RpcProcessor<ReadEventRequest> {
    private final DataService dataService;
    public ReadEventRequestProcessor(DataService dataService) {
        super();
        this.dataService = dataService;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, ReadEventRequest request) {
        final DataClosure closure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        try {
            this.dataService.readEvent(request, closure);
        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String interest() {
        return ReadEventRequest.class.getName();
    }
}
