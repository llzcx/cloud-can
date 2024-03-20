package ccw.serviceinnovation.node.server.db.queue;

import lombok.Data;
import org.apache.commons.lang.NotImplementedException;

@Data
public abstract class RPCTask implements Runnable {

    /**
     * 用于hash
     */
    private String key;
}
