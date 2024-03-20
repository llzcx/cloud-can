package service.raft.request;

import lombok.Data;
import service.raft.request.type.Write;

import java.io.Serializable;

@Data
public class ReadEventRequest implements Serializable, JRaftRpcReq, Write {
    private static final long serialVersionUID = -6597003954824547294L;
    private String eventId;
    private String objectKey;

    public ReadEventRequest(String eventId, String objectKey) {
        this.eventId = eventId;
        this.objectKey = objectKey;
    }

    @Override
    public String key() {
        return eventId;
    }
}
