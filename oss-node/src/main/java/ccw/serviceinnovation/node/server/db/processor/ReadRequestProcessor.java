package ccw.serviceinnovation.node.server.db.processor;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.ReadRequest;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class ReadRequestProcessor implements RpcProcessor<ReadRequest> {
    private final DataService dataService;

    public ReadRequestProcessor(DataService neService) {
        super();
        this.dataService = neService;
    }
    @Override
    public void handleRequest(RpcContext rpcCtx, ReadRequest request) {
        final DataClosure closure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        try {
            this.dataService.read(request, closure);
        } catch (IOException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String interest() {
        return ReadRequest.class.getName();
    }
}
