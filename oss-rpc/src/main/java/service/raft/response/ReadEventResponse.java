package service.raft.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
@Data
@ToString
public class ReadEventResponse implements Serializable, JRaftRpcRes{
    private Long realSize;

    public ReadEventResponse(Long realSize) {
        this.realSize = realSize;
    }

}
