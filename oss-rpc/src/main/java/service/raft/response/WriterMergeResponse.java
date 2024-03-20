package service.raft.response;

import ccw.serviceinnovation.common.request.ResultCode;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
@Data
@ToString
public class WriterMergeResponse implements Serializable, JRaftRpcRes{
    private Boolean mergeSuccess;
    private ResultCode resultCode;

    public WriterMergeResponse(Boolean mergeSuccess, ResultCode resultCode) {
        this.mergeSuccess = mergeSuccess;
        this.resultCode = resultCode;
    }
}
