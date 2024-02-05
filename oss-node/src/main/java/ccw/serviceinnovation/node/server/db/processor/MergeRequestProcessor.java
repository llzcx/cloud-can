package ccw.serviceinnovation.node.server.db.processor;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.MergeRequest;

public class MergeRequestProcessor implements RpcProcessor<MergeRequest> {
    private final DataService dataService;
    public MergeRequestProcessor(DataService dataService) {
        super();
        this.dataService = dataService;
    }
    @Override
    public void handleRequest(RpcContext rpcCtx, MergeRequest request) {
        final DataClosure dataClosure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.dataService.merge(request,dataClosure);
    }

    @Override
    public String interest() {
        return MergeRequest.class.getName();
    }
}
