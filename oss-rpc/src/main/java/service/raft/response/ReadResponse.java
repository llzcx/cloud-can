package service.raft.response;

import lombok.Data;

import java.io.Serializable;


@Data
public class ReadResponse implements Serializable, JRaftRpcRes {
    private byte[] data;

    public ReadResponse(byte[] data) {
        this.data = data;
    }
}
