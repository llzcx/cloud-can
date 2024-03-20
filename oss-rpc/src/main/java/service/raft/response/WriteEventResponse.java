package service.raft.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
@Data
@ToString
public class WriteEventResponse implements Serializable, JRaftRpcRes{
    /**
     * 秒传逻辑
     */
    private Boolean secondTransmission;

    public WriteEventResponse(Boolean secondTransmission) {
        this.secondTransmission = secondTransmission;
    }
}
