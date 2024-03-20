package service.raft.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
@Data
@ToString
public class ReadFragmentResponse implements Serializable, JRaftRpcRes{
    private byte[] data;

    public ReadFragmentResponse(byte[] data) {
        this.data = data;
    }
}
