package service.raft.response;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
@Data
@ToString
public class WriteFragmentResponse implements Serializable, JRaftRpcRes{
}
