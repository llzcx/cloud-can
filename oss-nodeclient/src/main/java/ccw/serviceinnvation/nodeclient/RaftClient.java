package ccw.serviceinnvation.nodeclient;

import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
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
import service.raft.request.JRaftRpcReq;
import service.raft.request.ReadDelEventRequest;
import service.raft.request.ReadEventRequest;
import service.raft.request.ReadFragmentRequest;
import service.raft.rpc.DataGrpcHelper;
import service.raft.rpc.RpcResponse;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
@Slf4j
public class RaftClient {

    private String nacos;

    private NamingService namingService;

    private final ReentrantReadWriteLock mainLock = new ReentrantReadWriteLock();

    // groupName -> groupMap
    private List<OssGroup> groupList = new ArrayList<>();
    private ConcurrentHashMap<String, CliClientServiceImpl> rpcClientMap = new ConcurrentHashMap<>();

    public RaftClient() {
    }

    public RaftClient(String nacos) {
        this.nacos = nacos;
    }

    public void listenChange(String nacos) {
        this.nacos = nacos;
        listenChange();
    }

    /**
     * 订阅服务变更
     */
    public void listenChange() {
        new Thread(() -> {
            try {
                DataGrpcHelper.initGRpc();
                namingService = NacosFactory.createNamingService(nacos);
                namingService.subscribe("oss", "raft", event -> {
                    if (event instanceof NamingEvent) {
                        ReentrantReadWriteLock.WriteLock writeLock = mainLock.writeLock();
                        try {
                            writeLock.lock();
                            groupList.clear();
                            rpcClientMap.clear();
                            NamingEvent namingEvent = (NamingEvent) event;
                            log.info("oss raft server changed.");
                            if (namingEvent.getInstances().size() != 0) {
                                for (Instance instance : namingEvent.getInstances()) {
                                    String groupName = instance.getClusterName();
                                    OssGroup ossGroup = new OssGroup(groupName, new ArrayList<>());
                                    ossGroup.getNodeList().add(instance.getIp() + ":" + instance.getPort());
                                    groupList.add(ossGroup);
                                }
                                groupList.forEach(ossGroup -> {
                                    final Configuration conf = new Configuration();
                                    String groupName = ossGroup.getGroupName();
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
                                    rpcClientMap.put(groupName, cliClientService);
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            writeLock.unlock();
                        }
                    } else {
                        log.info("no node server start.");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public PeerId getLeader(String groupName) {
        ReentrantReadWriteLock.ReadLock readLock = mainLock.readLock();
        try {
            readLock.lock();
            return RouteTable.getInstance().selectLeader(groupName);
        } finally {
            readLock.unlock();
        }
    }

    public CliClientServiceImpl getGroupClient(String groupName) {
        ReentrantReadWriteLock.ReadLock readLock = mainLock.readLock();
        try {
            readLock.lock();
            return rpcClientMap.get(groupName);
        } finally {
            readLock.unlock();
        }
    }


    public Object sync(final String groupName, JRaftRpcReq request,ResultCode resultCode) throws RemotingException, InterruptedException {
        CliClientServiceImpl cliClientService = getGroupClient(groupName);
        PeerId leader = getLeader(groupName);
        return sync(cliClientService,leader,request,resultCode);
    }
    public Object sync(final CliClientServiceImpl cliClientService,PeerId leader,JRaftRpcReq request,ResultCode resultCode) throws RemotingException, InterruptedException {
        RpcResponse rpcResponse = (RpcResponse) cliClientService.getRpcClient().invokeSync(leader.getEndpoint(), request, 5000000);
        if(!rpcResponse.getSuccess()){
            throw new OssException(resultCode);
        }
        return rpcResponse.getData();
    }


    public void shutDown() throws NacosException {
        namingService.shutDown();
        rpcClientMap.forEach((k, v) -> v.shutdown());
    }

    public int getGroupCount() {
        ReentrantReadWriteLock.ReadLock readLock = mainLock.readLock();
        try {
            readLock.lock();
            return groupList.size();
        } finally {
            readLock.unlock();
        }
    }

    public List<OssGroup> getList() {
        ReentrantReadWriteLock.ReadLock readLock = mainLock.readLock();
        try {
            readLock.lock();
            return groupList;
        } finally {
            readLock.unlock();
        }
    }

    public String getEventId(){
        return UUID.randomUUID().toString();
    }

    public void transferTo(ServletOutputStream outputStream,String groupName,String objectKey,long off,long size) throws RemotingException, InterruptedException, IOException {
        CliClientServiceImpl clientService = getGroupClient(groupName);
        PeerId leader = getLeader(groupName);
        String eventId = getEventId();
        //TODO 开启数据流事件
        Long realSize = (Long) sync(clientService,leader, new ReadEventRequest(true, eventId,objectKey), ResultCode.SERVER_EXCEPTION);
        if(size > realSize) throw new OssException(ResultCode.OFFSET_LIMIT);
        if(size == -1) size = realSize;
        if(off >= size){
            throw new IOException("offset exceeds limit.");
        }
        //TODO 传输数据流
        int TRANSFER_SIZE = 10*1024;
        for (long i = off; i < size; i+=TRANSFER_SIZE) {
            byte[] fragment = (byte[]) sync(clientService,leader,new ReadFragmentRequest(true,eventId,i,TRANSFER_SIZE),ResultCode.SERVER_EXCEPTION);
            outputStream.write(fragment);
        }
        //TODO 关闭数据流事件
        sync(clientService,leader,new ReadDelEventRequest(true,eventId),ResultCode.SERVER_EXCEPTION);
    }

    public static void main(String[] args) throws InterruptedException {
        RaftClient raftClient = new RaftClient("localhost:8848");
        raftClient.listenChange();
        Thread.sleep(1000 * 1000);
    }
}
