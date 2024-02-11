package service.raft.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class WriteDelEventRequest implements Serializable,JRaftRpcReq {
    private static final long serialVersionUID = -6597003954824547294L;

    private String eventId;

    public WriteDelEventRequest(String eventId) {
        this.eventId = eventId;
    }
}
