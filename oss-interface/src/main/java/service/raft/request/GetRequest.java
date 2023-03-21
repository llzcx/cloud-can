package service.raft.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class GetRequest implements Serializable {
    private static final long serialVersionUID = -6597003954824547294L;
    private boolean readOnSafe;
    private String etag;

    public GetRequest(boolean readOnSafe, String etag) {
        this.readOnSafe = readOnSafe;
        this.etag = etag;
    }
}
