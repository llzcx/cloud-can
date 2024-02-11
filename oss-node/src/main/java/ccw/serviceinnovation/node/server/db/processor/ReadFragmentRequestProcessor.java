package ccw.serviceinnovation.node.server.db.processor;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.ReadFragmentRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ReadFragmentRequestProcessor implements RpcProcessor<ReadFragmentRequest> {
    private final DataService dataService;

    public ReadFragmentRequestProcessor(DataService neService) {
        super();
        this.dataService = neService;
    }
    @Override
    public void handleRequest(RpcContext rpcCtx, ReadFragmentRequest request) {
        final DataClosure closure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        try {
            this.dataService.readFragment(request, closure);
        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String interest() {
        return ReadFragmentRequest.class.getName();
    }
}
