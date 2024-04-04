package ccw.serviceinnvation.nodeclient;

import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.loadbalance.OssGroup;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.error.RemotingException;
import service.raft.request.JRaftRpcReq;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

public interface GroupSentinel {
    void listenChange();

    PeerId getLeader(String groupName);

    Object sync(final String groupName, JRaftRpcReq request, ResultCode resultCode) throws RemotingException, InterruptedException;

    void transferTo(ServletOutputStream outputStream, String groupName, String objectKey, long off, long size) throws RemotingException, InterruptedException, IOException;

    OssGroup find(String etag);

    void balanceAdjustment(String newGroupName);


}
