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
import service.raft.request.JRaftRpcReq;
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

    public void listenChange(){
        try {
            namingService = NacosFactory.createNamingService(nacos);
            namingService.subscribe("oss","raft", event -> {
                if (event instanceof NamingEvent) {
                    NamingEvent namingEvent = (NamingEvent) event;
                    log.info("oss raft server changed.");
                    String groupName = namingEvent.getGroupName();
                    OssGroup ossGroup = new OssGroup();
                    ossGroup.setGroupName(groupName);
                    List<String> list = new ArrayList<>();
                    StringBuilder servers = new StringBuilder();
                    for (Instance instance : namingEvent.getInstances()) {
                        String addr = instance.getIp()+":"+instance.getPort();
                        list.add(addr);
                        servers.append(addr).append(",");
                    }
                    ossGroup.setNodeList(list);
                    groupMap.put(groupName,ossGroup);
                    final Configuration conf = new Configuration();
                    if (!conf.parse(servers.substring(0,servers.length()-1))) {
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
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        RpcResponse rpcResponse = (RpcResponse) cliClientService.getRpcClient().invokeSync(leader.getEndpoint(), request, 5000000);
        return rpcResponse.getData();
    }

    public void  shutDown() throws NacosException {
        namingService.shutDown();
        rpcClientMap.forEach((k,v)->v.shutdown());
    }

    public static void main(String[] args) {
        new RaftClient("localhost:8848");
    }
}
