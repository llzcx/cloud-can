package service.raft.response;

import ccw.serviceinnovation.common.request.ResultCode;
import lombok.Data;
import lombok.ToString;
import service.raft.request.JRaftRpcReq;

import java.io.Serializable;

@Data
@ToString
public class DelResponse implements Serializable, JRaftRpcRes {
    private Boolean delSuccess;
    private ResultCode resultCode;

    public DelResponse(Boolean delSuccess, ResultCode resultCode) {
        this.delSuccess = delSuccess;
        this.resultCode = resultCode;
    }
}
