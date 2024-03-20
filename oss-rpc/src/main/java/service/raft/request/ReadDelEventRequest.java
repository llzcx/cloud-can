package service.raft.request;

import lombok.Data;
import service.raft.request.type.Other;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class ReadDelEventRequest implements Serializable,JRaftRpcReq, Other {
    private static final long serialVersionUID = -6597003954824547294L;
    private String eventId;

    public ReadDelEventRequest(String eventId) {
        this.eventId = eventId;
    }

    @Override
    public String key() {
        return eventId;
    }
}
