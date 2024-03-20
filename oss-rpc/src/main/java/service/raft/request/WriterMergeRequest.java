package service.raft.request;

import lombok.Data;
import service.raft.request.type.Write;

import java.io.Serializable;
@Data
public class WriterMergeRequest implements Serializable,JRaftRpcReq, Write {
    private static final long serialVersionUID = -6597003954824547294L;


    public WriterMergeRequest(String eventId, String objectKey) {
        this.eventId = eventId;
        this.objectKey = objectKey;
    }

    private String eventId;

    private String objectKey;

    @Override
    public String key() {
        return objectKey;
    }
}
