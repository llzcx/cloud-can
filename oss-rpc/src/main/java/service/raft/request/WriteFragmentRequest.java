package service.raft.request;

import lombok.Data;
import service.raft.request.type.Other;

import java.io.Serializable;

/**
 * 分片上传请求
 */
@Data
public class WriteFragmentRequest implements Serializable, JRaftRpcReq, Other {
    private static final long serialVersionUID = -6597003954824547294L;
    /**
     * 此次上传事件的唯一id
     */
    private String eventId;
    /**
     * 分片数据
     */
    private byte[] fragment;
    /**
     * 偏移量
     */
    private Long off;

    private Integer chunk;

    public WriteFragmentRequest(String eventId, byte[] fragment, Long off, Integer chunk) {
        this.eventId = eventId;
        this.fragment = fragment;
        this.off = off;
    }

    @Override
    public String key() {
        return eventId;
    }
}
