package service.raft.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadRequest implements Serializable,JRaftRpcReq {
    private static final long serialVersionUID = -6597003954824547294L;
    /**
     * 二进制数据
     */
    private byte[] data;
    /**
     * hash值
     */
    private String etag;
    /**
     * 是否使用rs纠删码技术分片
     */
    private boolean rs;
}
