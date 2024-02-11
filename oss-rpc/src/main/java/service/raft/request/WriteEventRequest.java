package service.raft.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class WriteEventRequest implements Serializable, JRaftRpcReq {
    private static final long serialVersionUID = -6597003954824547294L;

    /**
     * 此次上传事件的唯一id
     */
    private String eventId;
    private String nodeObjectKey;
    private Long size;

    public WriteEventRequest(String nodeObjectKey, String eventId,Long size) {
        this.nodeObjectKey = nodeObjectKey;
        this.eventId = eventId;
        this.size = size;
    }
}
