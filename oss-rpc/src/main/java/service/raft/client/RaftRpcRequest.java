package service.raft.client;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.common.nacos.TrackerService;
import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.error.RemotingException;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import com.alipay.sofa.jraft.util.Endpoint;
import lombok.Data;

import service.raft.request.DelRequest;
import service.raft.request.GetRequest;
import service.raft.request.JRaftRpcReq;
import service.raft.rpc.RpcResponse;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author 陈翔
 */
public class RaftRpcRequest {

    public static ConcurrentHashMap<String, CliClientServiceImpl> mp = new ConcurrentHashMap<>();

    public static void init(String naocosPath) {
        Map<String, List<Host>> allJraftList = TrackerService.getAllJraftList(naocosPath);
        for (Map.Entry<String, List<Host>> stringListEntry : allJraftList.entrySet()) {
            String group = stringListEntry.getKey();
            List<Host> nodeList = stringListEntry.getValue();
            StringBuilder addrList = new StringBuilder();
            for (int i = 0; i < nodeList.size(); i++) {
                addrList.append(nodeList.get(i).getIp()).append(":").append(nodeList.get(i).getPort());
                if (i != nodeList.size() - 1) {
                    addrList.append(",");
                }
            }
            final Configuration conf = new Configuration();
            if (!conf.parse(addrList.toString())) {
                throw new IllegalArgumentException("Fail to parse conf:" + addrList);
            }
            RouteTable.getInstance().updateConfiguration(group, conf);
            final CliClientServiceImpl cliClientService = new CliClientServiceImpl();
            cliClientService.init(new CliOptions());
            mp.put(group, cliClientService);
        }
    }


    @Data
    public static class RaftRpcRequestBo {
        private CliClientServiceImpl cliClientService;
        private PeerId peerId;
    }

    public static RaftRpcRequestBo getLeader(String groupId) {
        //获取所有节点 groupName ==> node list
        RaftRpcRequestBo raftRpcRequestBo = new RaftRpcRequestBo();
        final CliClientServiceImpl cliClientService = mp.get(groupId);
        try {
            if (!RouteTable.getInstance().refreshLeader(cliClientService, groupId, 1000).isOk()) {
                throw new IllegalStateException("Refresh leader failed");
            }
        } catch (InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
        final PeerId leader = RouteTable.getInstance().selectLeader(groupId);
        raftRpcRequestBo.setCliClientService(cliClientService);
        raftRpcRequestBo.setPeerId(leader);
        return raftRpcRequestBo;
    }

    public static Object sync(String groupId, final PeerId leader,JRaftRpcReq request) throws RemotingException, InterruptedException {
        RpcResponse rpcResponse = (RpcResponse) getLeader(groupId).cliClientService.getRpcClient().invokeSync(leader.getEndpoint(), request, 5000000);
        return rpcResponse.getData();
    }


    public static Object sync(CliClientServiceImpl cliClientService, final PeerId leader,JRaftRpcReq request) throws RemotingException, InterruptedException {
        System.out.println(cliClientService.getRpcClient());
        System.out.println(request);
        System.out.println(leader.getEndpoint());
        RpcResponse rpcResponse = (RpcResponse) cliClientService.getRpcClient().invokeSync(leader.getEndpoint(), request, 5000000);
        return rpcResponse.getData();
    }
}
