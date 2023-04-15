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

import service.constant.Addr;
import service.raft.request.DelRequest;
import service.raft.request.GetRequest;
import service.raft.request.SaveRequest;
import service.raft.rpc.RpcResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author 陈翔
 */
public class RaftRpcRequest {

    public static HashMap<String, CliClientServiceImpl> mp = new HashMap<>();

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

    public static RaftRpcRequestBo getLeader(String naocosPath, String groupId) {
        //获取所有节点 groupName ==> node list
        RaftRpcRequestBo raftRpcRequestBo = new RaftRpcRequestBo();
        final CliClientServiceImpl cliClientService = mp.get(groupId);
        try {
            if (!RouteTable.getInstance().refreshLeader(cliClientService, groupId, 1000).isOk()) {
                throw new IllegalStateException("Refresh leader failed");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        final PeerId leader = RouteTable.getInstance().selectLeader(groupId);
        raftRpcRequestBo.setCliClientService(cliClientService);
        raftRpcRequestBo.setPeerId(leader);
        return raftRpcRequestBo;
    }


    /**
     * @param cliClientService
     * @param leader
     * @param etag
     * @param locationVo       String fileUrl = "https://"+locationVo.getIp()+":"+locationVo.getPort()+
     *                         "/object/download_temp/"+dataOperation.getEtag();
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    public static boolean save(final CliClientServiceImpl cliClientService, final PeerId leader, String etag, LocationVo locationVo) throws RemotingException, InterruptedException {
        Endpoint endpoint = leader.getEndpoint();
        SaveRequest saveRequest = new SaveRequest(etag, locationVo);
        RpcResponse rpcResponse = (RpcResponse) cliClientService.getRpcClient().invokeSync(endpoint, saveRequest, 5000000);
        return rpcResponse.getSuccess();
    }

    public static LocationVo get(final CliClientServiceImpl cliClientService, final PeerId leader, String etag) throws RemotingException, InterruptedException {
        Endpoint endpoint = leader.getEndpoint();
        GetRequest getRequest = new GetRequest(true, etag);
        RpcResponse rpcResponse = (RpcResponse) cliClientService.getRpcClient().invokeSync(endpoint, getRequest, 5000000);

        LocationVo data = (LocationVo) rpcResponse.getData();

        return data;
    }

    public static boolean del(final CliClientServiceImpl cliClientService, final PeerId leader, String etag) throws RemotingException, InterruptedException {
        Endpoint endpoint = leader.getEndpoint();
        DelRequest delRequest = new DelRequest(etag);
        RpcResponse rpcResponse = (RpcResponse) cliClientService.getRpcClient().invokeSync(endpoint, delRequest, 5000000);

        return rpcResponse.getSuccess();
    }

}
