package service.raft.request;

import lombok.Data;
import service.raft.request.type.Write;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class DelRequest implements Serializable,JRaftRpcReq, Write {
    private static final long serialVersionUID = -6597003954824547294L;
    private String nodeObjectKey;

    public DelRequest(String nodeObjectKey) {
        this.nodeObjectKey = nodeObjectKey;
    }

    @Override
    public String key() {
        return nodeObjectKey;
    }
}
