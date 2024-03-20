package ccw.serviceinnovation.node.server.db.queue;

import java.util.concurrent.Future;

public interface HandlerQueue {
    Future<?> submit(RPCTask rpcTask);
}
