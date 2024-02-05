package ccw.serviceinnvation.nodeclient;

import ccw.serviceinnovation.loadbalance.OssGroup;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alipay.sofa.jraft.RouteTable;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.error.RemotingException;
import com.alipay.sofa.jraft.option.CliOptions;
import com.alipay.sofa.jraft.rpc.impl.cli.CliClientServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantLock;

import service.raft.request.GetRequest;
import service.raft.request.JRaftRpcReq;
import service.raft.rpc.DataGrpcHelper;
import service.raft.rpc.RpcResponse;

@Data
@Slf4j
public class RaftClient {

    private String nacos;

    private NamingService namingService;

    // groupName -> groupMap
    private ConcurrentHashMap<String, OssGroup> groupMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, CliClientServiceImpl> rpcClientMap = new ConcurrentHashMap<>();

    public RaftClient(String nacos) {
        this.nacos = nacos;
    }

    /**
     * 订阅服务变更
     */
    public void listenChange(){
        new Thread(() -> {
            try {
                DataGrpcHelper.initGRpc();
                namingService = NacosFactory.createNamingService(nacos);
                namingService.subscribe("oss","raft", event -> {
                    if (event instanceof NamingEvent) {
                        groupMap.clear();
                        rpcClientMap.clear();
                        NamingEvent namingEvent = (NamingEvent) event;
                        log.info("oss raft server changed.");
                        if(namingEvent.getInstances().size()!=0){
                            for (Instance instance : namingEvent.getInstances()) {
                                String groupName = instance.getClusterName();
                                OssGroup ossGroup = groupMap.computeIfAbsent(groupName, key -> new OssGroup(key, new ArrayList<>()));
                                String addr = instance.getIp()+":"+instance.getPort();
                                ossGroup.getNodeList().add(addr);
                            }
                            groupMap.forEach((groupName,ossGroup)->{
                                final Configuration conf = new Configuration();
                                if (!conf.parse(ossGroup.getConf())) {
                                    throw new IllegalArgumentException("Fail to parse conf:");
                                }
                                RouteTable.getInstance().updateConfiguration(groupName, conf);
                                final CliClientServiceImpl cliClientService = new CliClientServiceImpl();
                                cliClientService.init(new CliOptions());
                                try {
                                    RouteTable.getInstance().refreshLeader(cliClientService, groupName, 1000).isOk();
                                } catch (InterruptedException | TimeoutException e) {
                                    throw new RuntimeException(e);
                                }
                                rpcClientMap.put(groupName,cliClientService);
                                try {
                                    sync("cxoss",new GetRequest(true,"test"));
                                } catch (RemotingException | InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        }
                    }else{
                        log.info("no node server start.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public PeerId getLeader(String groupName){
        return RouteTable.getInstance().selectLeader(groupName);
    }

    public CliClientServiceImpl getGroupClient(String groupName){
        return rpcClientMap.get(groupName);
    }


    public Object sync(final String groupName,JRaftRpcReq request) throws RemotingException, InterruptedException {
        CliClientServiceImpl cliClientService = getGroupClient(groupName);
        PeerId leader = getLeader(groupName);
        if(cliClientService == null || leader == null){
            throw new RuntimeException("no group:"+groupName);
        }
        RpcResponse rpcResponse = (RpcResponse) cliClientService.getRpcClient().invokeSync(leader.getEndpoint(), request, 5000000);
        return rpcResponse.getData();
    }

    public void  shutDown() throws NacosException {
        namingService.shutDown();
        rpcClientMap.forEach((k,v)->v.shutdown());
    }

    public static void main(String[] args) throws InterruptedException {
        RaftClient raftClient = new RaftClient("localhost:8848");
        raftClient.listenChange();
        Thread.sleep(1000*1000);
    }
}
