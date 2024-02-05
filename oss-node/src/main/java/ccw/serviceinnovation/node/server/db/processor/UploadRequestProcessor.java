package ccw.serviceinnovation.node.server.db.processor;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.UploadRequest;

public class UploadRequestProcessor implements RpcProcessor<UploadRequest>  {
    private final DataService dataService;
    public UploadRequestProcessor(DataService dataService) {
        super();
        this.dataService = dataService;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, UploadRequest request) {
        final DataClosure dataClosure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.dataService.upload(request,dataClosure);
    }

    @Override
    public String interest() {
        return UploadRequest.class.getName();
    }
}
