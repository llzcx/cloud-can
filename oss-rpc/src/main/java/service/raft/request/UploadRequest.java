package service.raft.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadRequest implements Serializable,JRaftRpcReq {
    private static final long serialVersionUID = -6597003954824547294L;

    public UploadRequest(byte[] data, String nodeObjectKey) {
        this.data = data;
        this.nodeObjectKey = nodeObjectKey;
    }

    /**
     * 二进制数据
     */
    private byte[] data;
    /**
     * hash值
     */
    private String nodeObjectKey;
}
