package ccw.serviceinnovation.node.server.db.apply;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataOperation;
import ccw.serviceinnovation.node.server.db.ServiceHandler;
import ccw.serviceinnovation.node.server.db.queue.HandlerQueue;
import ccw.serviceinnovation.node.server.db.queue.RPCTask;
import ccw.serviceinnovation.node.server.db.queue.RPCTaskQueueImpl;
import ccw.serviceinnovation.node.util.FutureUtil;
import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import lombok.extern.slf4j.Slf4j;
import service.raft.request.JRaftRpcReq;
import service.raft.request.type.OnlyRead;
import service.raft.request.type.Write;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * 读写分离
 */
@Slf4j
public class ReadWriteSeparationImpl implements OnApplyHandler {

    HandlerQueue reader;
    HandlerQueue writer;
    HandlerQueue other;

    public ReadWriteSeparationImpl(int read, int write, int oth) {
        reader = new RPCTaskQueueImpl("reader", read);
        writer = new RPCTaskQueueImpl("writer", write);
        other = new RPCTaskQueueImpl("other", oth);
    }

    @Override
    public void batching(final Iterator iter) {
        List<Future<?>> list = new ArrayList<>();
        while (iter.hasNext()) {
            DataOperation dataOperation = null;
            DataClosure closure = null;
            // TODO 解析
            if (iter.done() != null) {
                closure = (DataClosure) iter.done();
                dataOperation = closure.getDataOperation();
            } else {
                try {
                    dataOperation = SerializerManager.getSerializer(SerializerManager.Hessian2).deserialize(iter.getData().array(),
                            DataOperation.class.getName());
                } catch (final CodecException e) {
                    log.error("Fail to decode DataRequest", e);
                }
            }
            // TODO 任务下发
            if (dataOperation != null) {
                JRaftRpcReq request = dataOperation.getRequest();
                DataClosure finalClosure = closure;
                RPCTask applyTask = new RPCTask() {
                    @Override
                    public void run() {
                        try {
                            Object res = ServiceHandler.invoke(request);
                            if (finalClosure != null) {
                                finalClosure.success(res);
                                finalClosure.run(Status.OK());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (finalClosure != null) {
                                System.out.println("发送回应：失败");
                                finalClosure.failure("ERROR");
                                finalClosure.run(Status.OK());
                            }
                            throw new RuntimeException("apply error");
                        }
                    }
                };
                applyTask.setKey(request.key());
                if (request instanceof OnlyRead) {
                    list.add(reader.submit(applyTask));
                } else if (request instanceof Write) {
                    list.add(writer.submit(applyTask));
                } else {
                    list.add(other.submit(applyTask));
                }
            }
            iter.next();
        }
        try {
            FutureUtil.await(list);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
