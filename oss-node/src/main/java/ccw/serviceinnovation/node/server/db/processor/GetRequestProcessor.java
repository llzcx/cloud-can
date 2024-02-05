package ccw.serviceinnovation.node.server.db.processor;


import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.GetRequest;

/**
 * @author 陈翔
 */
public class GetRequestProcessor implements RpcProcessor<GetRequest> {
    private final DataService dataService;
    public GetRequestProcessor(DataService dataService) {
        super();
        this.dataService = dataService;
    }
    @Override
    public void handleRequest(RpcContext rpcCtx, GetRequest request) {
        final DataClosure dataClosure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.dataService.get(request,dataClosure);
    }

    @Override
    public String interest() {
        return GetRequest.class.getName();
    }
}
