package ccw.serviceinnovation.node.server.db.processor;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.WriteEventRequest;

public class WriteEventRequestProcessor implements RpcProcessor<WriteEventRequest> {
    private final DataService dataService;

    public WriteEventRequestProcessor(DataService neService) {
        super();
        this.dataService = neService;
    }
    @Override
    public void handleRequest(RpcContext rpcCtx, WriteEventRequest request) {
        final DataClosure closure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.dataService.writeEvent(request, closure);
    }

    @Override
    public String interest() {
        return WriteEventRequest.class.getName();
    }
}
