package ccw.serviceinnovation.node.server.db.processor;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.FragmentRequest;

public class FragmentRequestProcessor implements RpcProcessor<FragmentRequest> {
    private final DataService dataService;
    public FragmentRequestProcessor(DataService dataService) {
        super();
        this.dataService = dataService;
    }
    @Override
    public void handleRequest(RpcContext rpcCtx, FragmentRequest request) {
        final DataClosure dataClosure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.dataService.fragment(request,dataClosure);
    }

    @Override
    public String interest() {
        return FragmentRequest.class.getName();
    }
}
