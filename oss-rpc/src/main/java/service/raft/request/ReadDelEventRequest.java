package service.raft.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class ReadDelEventRequest implements Serializable,JRaftRpcReq {
    private static final long serialVersionUID = -6597003954824547294L;
    private boolean readOnSafe;
    private String eventId;

    public ReadDelEventRequest(boolean readOnSafe, String eventId) {
        this.readOnSafe = readOnSafe;
        this.eventId = eventId;
    }
}
