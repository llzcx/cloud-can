package service.raft.request;

import lombok.Data;
import service.raft.request.type.Other;
import service.raft.request.type.Write;

import java.io.Serializable;

@Data
public class WriteDelEventRequest implements Serializable, JRaftRpcReq, Other {
    private static final long serialVersionUID = -6597003954824547294L;

    private String eventId;

    public WriteDelEventRequest(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String key() {
        return eventId;
    }
}
