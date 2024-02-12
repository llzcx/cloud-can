/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.node.server.db.processor.*;
import com.alipay.sofa.jraft.Node;
import com.alipay.sofa.jraft.RaftGroupService;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import com.alipay.sofa.jraft.rpc.RaftRpcServerFactory;
import com.alipay.sofa.jraft.rpc.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import service.raft.rpc.DataGrpcHelper;
import service.raft.rpc.RpcResponse;

import java.io.File;
import java.io.IOException;

/**
 * Counter server that keeps a counter value in a raft group.
 *
 * @author boyan (boyan@alibaba-inc.com)
 * <p>
 * 2018-Apr-09 4:51:02 PM
 */
@Slf4j
public class DataServer {

    private RaftGroupService raftGroupService;
    private Node node;
    private DataStateMachine fsm;

    public DataServer(final String dataPath, final String groupId, final PeerId serverId, final NodeOptions nodeOptions)
            throws IOException {
        // init raft data path, it contains log,meta,snapshot
        FileUtils.forceMkdir(new File(dataPath));

        // here use same RPC server for raft and business. It also can be seperated generally
        final RpcServer rpcServer = RaftRpcServerFactory.createRaftRpcServer(serverId.getEndpoint());
        // GrpcServer need init marshaller
        DataGrpcHelper.initGRpc();
        DataGrpcHelper.setRpcServer(rpcServer);

        // register business processor
        DataService dataService = new DataServiceImpl(this);


        rpcServer.registerProcessor(new ReadEventRequestProcessor(dataService));
        rpcServer.registerProcessor(new ReadFragmentRequestProcessor(dataService));
        rpcServer.registerProcessor(new ReadDelEventRequestProcessor(dataService));

        rpcServer.registerProcessor(new UploadRequestProcessor(dataService));

        rpcServer.registerProcessor(new DelRequestProcessorProcessor(dataService));

        rpcServer.registerProcessor(new WriteEventRequestProcessor(dataService));
        rpcServer.registerProcessor(new WriteFragmentRequestProcessor(dataService));
        rpcServer.registerProcessor(new WriteDelEventRequestProcessor(dataService));
        rpcServer.registerProcessor(new WriteMergeRequestProcessor(dataService));
        // init state machine
        this.fsm = new DataStateMachine();
        // set fsm to nodeOptions
        nodeOptions.setFsm(this.fsm);
        // set storage path (log,meta,snapshot)
        // log, must
        nodeOptions.setLogUri(dataPath + File.separator + "log");
        // meta, must
        nodeOptions.setRaftMetaUri(dataPath + File.separator + "raft_meta");
        // snapshot, optional, generally recommended
        nodeOptions.setSnapshotUri(dataPath + File.separator + "snapshot");
        // init raft group service framework
        this.raftGroupService = new RaftGroupService(groupId, serverId, nodeOptions, rpcServer);
        // start raft node
        this.node = this.raftGroupService.start();
    }

    public DataStateMachine getFsm() {
        return this.fsm;
    }

    public Node getNode() {
        return this.node;
    }

    public RaftGroupService RaftGroupService() {
        return this.raftGroupService;
    }

    /**
     * Redirect request to new leader
     */
    public RpcResponse redirect() {
        if (this.node != null) {
            final PeerId leader = this.node.getLeaderId();
            if (leader != null) {
                return RpcResponse.error("重定向", leader.toString());
            }
        }
        return RpcResponse.error("node为空", null);
    }

    /**
     * 启动Jraft服务
     * @throws IOException
     */
    public static void start() throws IOException {
        final String dataPath = RegisterConstant.RAFT_LOG_DISK;
        final String groupId = RegisterConstant.GROUP_NAME;
        final String serverIdStr = RegisterConstant.ADDR;
        final String initConfStr = RegisterConstant.GROUP_CLUSTER;
        final Long electionTimeoutMs = RegisterConstant.ELECTION_TIMEOUT;
        log.info("GroupName is {},NodeList is {}", groupId, serverIdStr);
        final NodeOptions nodeOptions = new NodeOptions();
        // for test, modify some params
        // set election timeout to 1s
        nodeOptions.setElectionTimeoutMs(Math.toIntExact(electionTimeoutMs));
        // disable CLI service。
        nodeOptions.setDisableCli(false);
        // parse server address
        final PeerId serverId = new PeerId();
        if (!serverId.parse(serverIdStr)) {
            throw new IllegalArgumentException("Fail to parse serverId:" + serverIdStr);
        }
        final Configuration initConf = new Configuration();
        if (!initConf.parse(initConfStr)) {
            throw new IllegalArgumentException("Fail to parse initConf:" + initConfStr);
        }
        // set cluster configuration
        nodeOptions.setInitialConf(initConf);
        // start raft server
        final DataServer dataServer;
        try {
            dataServer = new DataServer(dataPath, groupId, serverId, nodeOptions);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // GrpcServer need block to prevent process exit
        DataGrpcHelper.blockUntilShutdown();
    }
}
