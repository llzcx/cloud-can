package service.raft.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
@Data
@ToString
public class ReadDelEventResponse implements Serializable, JRaftRpcRes{
    private Boolean delSuccess;

    public ReadDelEventResponse(Boolean delSuccess) {
        this.delSuccess = delSuccess;
    }
}
