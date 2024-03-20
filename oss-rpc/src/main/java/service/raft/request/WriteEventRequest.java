package service.raft.request;

import lombok.Data;
import service.raft.request.type.Write;

import java.io.Serializable;

@Data
public class WriteEventRequest implements Serializable, JRaftRpcReq, Write {
    private static final long serialVersionUID = -6597003954824547294L;

    /**
     * 此次上传事件的唯一id
     */
    private String eventId;
    private String nodeObjectKey;
    private Long size;
    private Integer chunks;

    public WriteEventRequest(String nodeObjectKey, String eventId,Long size,Integer chunks) {
        this.nodeObjectKey = nodeObjectKey;
        this.eventId = eventId;
        this.size = size;
    }

    @Override
    public String key() {
        return nodeObjectKey;
    }
}
