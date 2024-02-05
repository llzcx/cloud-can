package service.raft.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class DelRequest implements Serializable,JRaftRpcReq {
    private static final long serialVersionUID = -6597003954824547294L;
    private String etag;

    public DelRequest(String etag) {
        this.etag = etag;
    }
}
