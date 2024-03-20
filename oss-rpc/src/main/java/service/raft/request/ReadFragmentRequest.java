package service.raft.request;

import lombok.Data;
import service.raft.request.type.OnlyRead;

import java.io.Serializable;

@Data
public class ReadFragmentRequest implements Serializable,JRaftRpcReq, OnlyRead {
    private static final long serialVersionUID = -6597003954824547294L;
    private boolean readOnSafe;
    private String eventId;
    private long off;
    private int size;

    public ReadFragmentRequest(boolean readOnSafe, String eventId,long off,int size) {
        this.readOnSafe = readOnSafe;
        this.eventId = eventId;
        this.off = off;
        this.size = size;
    }

    @Override
    public String key() {
        return eventId;
    }
}
