package service.raft.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 分片上传请求
 */
@Data
public class FragmentRequest implements Serializable ,JRaftRpcReq{
    private static final long serialVersionUID = -6597003954824547294L;
    /**
     * 此次上传事件的唯一id
     */
    private String eventId;
    /**
     * 分片数据
     */
    private byte[] fragment;
}
