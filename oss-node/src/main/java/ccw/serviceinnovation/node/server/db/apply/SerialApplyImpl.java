package ccw.serviceinnovation.node.server.db.apply;

import ccw.serviceinnovation.node.server.db.DataClosure;
import ccw.serviceinnovation.node.server.db.DataOperation;
import ccw.serviceinnovation.node.server.db.ServiceHandler;
import com.alipay.remoting.exception.CodecException;
import com.alipay.remoting.serialization.SerializerManager;
import com.alipay.sofa.jraft.Iterator;
import com.alipay.sofa.jraft.Status;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * 串行应用状态机
 */
@Slf4j
public class SerialApplyImpl implements OnApplyHandler{

    @Override
    public void batching(Iterator iter) {
        while (iter.hasNext()) {
            DataOperation dataOperation = null;
            DataClosure closure = null;
            if (iter.done() != null) {
                // This task is applied by this node, get value from closure to avoid additional parsing.
                closure = (DataClosure) iter.done();
                dataOperation = closure.getDataOperation();
            } else {
                // Have to parse FetchAddRequest from this user log.
                final ByteBuffer data = iter.getData();
                try {
                    dataOperation = SerializerManager.getSerializer(SerializerManager.Hessian2).deserialize(data.array(),
                            DataOperation.class.getName());
                } catch (final CodecException e) {
                    log.error("Fail to decode DataRequest", e);
                }
                // follower ignore read operation
                if (dataOperation != null && dataOperation.isRead()) {
                    iter.next();
                    continue;
                }
            }
            if (dataOperation != null) {
                try {
                    Object res = ServiceHandler.invoke(dataOperation.request);
                    if (closure != null) {
                        closure.success(res);
                        closure.run(Status.OK());
                    }
                } catch (Exception e) {
                    if (closure != null) {
                        closure.failure("ERROR");
                    }
                    e.printStackTrace();
                }
            }
            iter.next();
        }
    }
}
