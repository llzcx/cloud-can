package ccw.serviceinnovation.node.server.db.processor;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.WriterMergeRequest;

public class WriteMergeRequestProcessor implements RpcProcessor<WriterMergeRequest> {
    private final DataService dataService;
    public WriteMergeRequestProcessor(DataService dataService) {
        super();
        this.dataService = dataService;
    }
    @Override
    public void handleRequest(RpcContext rpcCtx, WriterMergeRequest request) {
        final DataClosure dataClosure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.dataService.writeMerge(request,dataClosure);
    }

    @Override
    public String interest() {
        return WriterMergeRequest.class.getName();
    }
}
