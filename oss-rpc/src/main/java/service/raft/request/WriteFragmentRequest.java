package service.raft.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 分片上传请求
 */
@Data
public class WriteFragmentRequest implements Serializable ,JRaftRpcReq{
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

    public WriteFragmentRequest(String eventId, byte[] fragment, Long off) {
        this.eventId = eventId;
        this.fragment = fragment;
        this.off = off;
    }
}
