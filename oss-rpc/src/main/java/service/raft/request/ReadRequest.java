package service.raft.request;

import lombok.Data;
import service.raft.request.type.OnlyRead;

import java.io.Serializable;

@Data
public class ReadRequest implements Serializable, JRaftRpcReq, OnlyRead {
    private static final long serialVersionUID = -6597003954824547294L;
    private String nodeObjectKey;
    private Boolean readOnlySafe;

    @Override
    public String key() {
        return nodeObjectKey;
    }
}
