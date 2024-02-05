package service.raft.request;

import java.io.Serializable;

public class EventRequest implements Serializable,JRaftRpcReq {
    private static final long serialVersionUID = -6597003954824547294L;

    /**
     * 此次上传事件的唯一id
     */
    private String eventId;
}
