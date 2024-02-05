package service.raft.client;

import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.error.RemotingException;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import service.raft.request.*;

import java.util.concurrent.TimeoutException;

public class ClientDemo {
    public static void main(String[] args) throws RemotingException, InterruptedException, TimeoutException {
        final Configuration conf = new Configuration();
        String group = "cxoss";
        if (!conf.parse("127.0.0.1:8021,127.0.0.1:8022,127.0.0.1:8023")) {
            throw new IllegalArgumentException("Fail to parse conf:");
        }
        RouteTable.getInstance().updateConfiguration(group, conf);
        final CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());
        RouteTable.getInstance().refreshLeader(cliClientService, group, 1000).isOk();
        final PeerId leader = RouteTable.getInstance().selectLeader(group);

        System.out.println(RaftRpcRequest.sync(cliClientService, leader, new GetRequest(true,"1")));
        System.out.println(RaftRpcRequest.sync(cliClientService, leader, new DelRequest("2")));
        System.out.println(RaftRpcRequest.sync(cliClientService, leader, new UploadRequest()));
        System.out.println(RaftRpcRequest.sync(cliClientService, leader, new EventRequest()));
        System.out.println(RaftRpcRequest.sync(cliClientService, leader, new FragmentRequest()));
        System.out.println(RaftRpcRequest.sync(cliClientService, leader, new DelEventRequest()));
        System.out.println(RaftRpcRequest.sync(cliClientService, leader, new MergeRequest()));
    }
}
