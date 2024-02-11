package ccw.serviceinnovation.node.server.db.processor;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.DelRequest;
import service.raft.request.ReadDelEventRequest;

import java.lang.reflect.InvocationTargetException;

public class ReadDelEventRequestProcessor implements RpcProcessor<ReadDelEventRequest> {
    private final DataService dataService;

    public ReadDelEventRequestProcessor(DataService neService) {
        super();
        this.dataService = neService;
    }
    @Override
    public void handleRequest(RpcContext rpcCtx, ReadDelEventRequest request) {
        final DataClosure closure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        try {
            this.dataService.readDelEvent(request, closure);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String interest() {
        return ReadDelEventRequest.class.getName();
    }
}
