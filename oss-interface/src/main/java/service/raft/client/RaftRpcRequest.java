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
import service.raft.request.SaveRequest;
import service.raft.rpc.RpcResponse;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * @author 陈翔
 */
public class RaftRpcRequest {


    @Data
    public static class RaftRpcRequestBo{
        private CliClientServiceImpl cliClientService;
        private PeerId peerId;
    }

    public static RaftRpcRequestBo getLeader(String naocosPath,String groupId){
        RaftRpcRequestBo raftRpcRequestBo = new RaftRpcRequestBo();
        Map<String, List<Host>> allJraftList = TrackerService.getAllJraftList(naocosPath);
        StringBuilder addr_list = new StringBuilder();
        List<Host> list = allJraftList.get(groupId);
        for (int i = 0; i < list.size(); i++) {
            addr_list.append("127.0.0.1:").append(list.get(i).getPort());
            if(i!=list.size()-1){
                addr_list.append(",");
            }
        }
        System.out.println("addr_list:"+addr_list);
        final Configuration conf = new Configuration();
        if (!conf.parse(addr_list.toString())) {
            throw new IllegalArgumentException("Fail to parse conf:" + addr_list);
        }
        RouteTable.getInstance().updateConfiguration(groupId, conf);
        final CliClientServiceImpl cliClientService = new CliClientServiceImpl();
        cliClientService.init(new CliOptions());
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
     *
     * @param cliClientService
     * @param leader
     * @param etag
     * @param locationVo
     *     String fileUrl = "https://"+locationVo.getIp()+":"+locationVo.getPort()+
     *                                 "/object/download_temp/"+dataOperation.getEtag();
     * @return
     * @throws RemotingException
     * @throws InterruptedException
     */
    public static boolean save(final CliClientServiceImpl cliClientService, final PeerId leader, String etag, LocationVo locationVo) throws RemotingException, InterruptedException {
        Endpoint endpoint = leader.getEndpoint();
        SaveRequest saveRequest = new SaveRequest(etag,locationVo);
        RpcResponse rpcResponse = (RpcResponse)cliClientService.getRpcClient().invokeSync(endpoint, saveRequest, 5000);
        System.out.println("result:"+rpcResponse);
        return rpcResponse.getSuccess();
    }

    public static LocationVo get(final CliClientServiceImpl cliClientService, final PeerId leader, String etag) throws RemotingException, InterruptedException {
        Endpoint endpoint = leader.getEndpoint();
        GetRequest getRequest = new GetRequest(true,etag);
        RpcResponse rpcResponse = (RpcResponse)cliClientService.getRpcClient().invokeSync(endpoint, getRequest, 5000);
        System.out.println("getData:"+rpcResponse.getData());
        LocationVo data = (LocationVo)rpcResponse.getData();
        System.out.println("result:"+rpcResponse);
        return data;
    }

    public static boolean del(final CliClientServiceImpl cliClientService, final PeerId leader, String etag) throws RemotingException, InterruptedException {
        Endpoint endpoint = leader.getEndpoint();
        DelRequest delRequest = new DelRequest(etag);
        RpcResponse rpcResponse = (RpcResponse)cliClientService.getRpcClient().invokeSync(endpoint, delRequest, 5000);
        System.out.println("result:"+rpcResponse);
        return rpcResponse.getSuccess();
    }

}
