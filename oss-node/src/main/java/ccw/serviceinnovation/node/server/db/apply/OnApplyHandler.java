package ccw.serviceinnovation.node.server.db.apply;

import com.alipay.sofa.jraft.Iterator;

public interface OnApplyHandler {
    void batching(final Iterator iter);
}
