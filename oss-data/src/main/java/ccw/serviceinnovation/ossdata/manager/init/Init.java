package ccw.serviceinnovation.ossdata.manager.init;

import ccw.serviceinnovation.common.util.IpUtils;
import ccw.serviceinnovation.common.util.http.FileUtil;
import ccw.serviceinnovation.ossdata.constant.OssDataConstant;
import ccw.serviceinnovation.ossdata.manager.raft.server.DataServer;
import ccw.serviceinnovation.ossdata.manager.raft.server.DataStateMachine;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alipay.sofa.jraft.conf.Configuration;
import com.alipay.sofa.jraft.entity.PeerId;
import com.alipay.sofa.jraft.option.NodeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import service.raft.rpc.DataGrpcHelper;

import java.io.File;

import static ccw.serviceinnovation.ossdata.constant.FilePrefixConstant.FILE_NOR;
import static ccw.serviceinnovation.ossdata.constant.OssDataConstant.POSITION;

/**
 * @author 陈翔
 */
@Component
@Slf4j
public class Init {

    public void fileInit(){
        File file1 = new File(OssDataConstant.JRAFT_DATA_PATH);
        File file2 = new File(POSITION);
        //先删除
        FileUtil.deleteFile(file1);
        FileUtil.deleteFile(file2);
        //创建目录
        if(!file1.exists()) {
            file1.mkdirs();
            System.out.println("目录:"+OssDataConstant.JRAFT_DATA_PATH+"初始化成功");
        }
        if(!file2.exists()) {
            file2.mkdirs();
            System.out.println("目录:"+OssDataConstant.POSITION+"初始化成功");
        }

    }


    public  void initFileKey() throws Exception {
        System.out.println("position:"+ POSITION);
        File fileDir = new File(POSITION);
        File[] files = fileDir.listFiles();
        for (File file : files) {
            String name = file.getName();
            if(name.startsWith(FILE_NOR)){
                String etag = name.substring(FILE_NOR.length());
                log.info("加入了etag:"+etag);
                DataStateMachine.dataMap.put(etag,file.getAbsolutePath());
            }
        }
    }



    public void initJraft() throws Exception{
        final String dataPath = OssDataConstant.JRAFT_DATA_PATH;
        final String groupId = OssDataConstant.GROUP;
        final String serverIdStr = OssDataConstant.RPC_ADDR;
        final String initConfStr = OssDataConstant.CLUSTER;
        log.info("dataPath:{},groupId:{},serverIdStr:{},initConfStr{}",dataPath,groupId,serverIdStr,initConfStr);

        final NodeOptions nodeOptions = new NodeOptions();
        // for test, modify some params
        // set election timeout to 1s
        nodeOptions.setElectionTimeoutMs(1000);
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
        final DataServer dataServer = new DataServer(dataPath, groupId, serverId, nodeOptions);
        System.out.println("Started counter server at port:" + dataServer.getNode().getNodeId().getPeerId().getPort());
        // GrpcServer need block to prevent process exit
        DataGrpcHelper.blockUntilShutdown();
    }

    public void registerNacos() throws NacosException {
        NamingService namingService = NamingFactory.createNamingService("101.35.43.156:8848");
        String ip = IpUtils.getIp(OssDataConstant.RPC_ADDR);
        Integer port = IpUtils.getPort(OssDataConstant.RPC_ADDR);
        Instance instance = new Instance();
        instance.setIp(ip);
        instance.setInstanceId("raft-rpc1");
        instance.setPort(port);
        instance.addMetadata("group", OssDataConstant.GROUP);
        instance.addMetadata("port", OssDataConstant.PORT);
        namingService.registerInstance("raft-rpc",instance);
        //更新元数据
//        trackerService.updateJraftMeta();
    }






}
