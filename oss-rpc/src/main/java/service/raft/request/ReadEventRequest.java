package service.raft.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class ReadEventRequest implements Serializable,JRaftRpcReq {
    private static final long serialVersionUID = -6597003954824547294L;
    private boolean readOnSafe;
    private String eventId;
    private String objectKey;

    public ReadEventRequest(boolean readOnSafe, String eventId,String objectKey) {
        this.readOnSafe = readOnSafe;
        this.eventId = eventId;
        this.objectKey = objectKey;
    }
}
