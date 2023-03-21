package ccw.serviceinnovation.ossdata.manager.raft.server.rpc;

import ccw.serviceinnovation.ossdata.manager.raft.server.DataClosure;
import ccw.serviceinnovation.ossdata.manager.raft.server.service.DataService;
import com.alipay.sofa.jraft.Status;
import com.alipay.sofa.jraft.rpc.RpcContext;
import com.alipay.sofa.jraft.rpc.RpcProcessor;
import service.raft.request.DelRequest;
import service.raft.request.GetRequest;
import service.raft.request.SaveRequest;

import java.io.Serializable;

/**
 * @author 陈翔
 */
public class SaveRequestProcessor implements RpcProcessor<SaveRequest> {
    private final DataService dataService;
    public SaveRequestProcessor(DataService neService) {
        super();
        this.dataService = neService;
    }

    @Override
    public void handleRequest(RpcContext rpcCtx, SaveRequest request) {
        final DataClosure dataClosure = new DataClosure() {
            @Override
            public void run(Status status) {
                rpcCtx.sendResponse(getResponse());
            }
        };
        this.dataService.save(request.getEtag(),request.getLocationVo(), dataClosure);
    }

    @Override
    public String interest() {
        return SaveRequest.class.getName();
    }
}
