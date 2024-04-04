package ccw.serviceinnovation.node.server.constant;

import ccw.serviceinnovation.common.util.IpUtils;
import ccw.serviceinnovation.node.bo.RsParam;
import ccw.serviceinnovation.node.util.MainParamsUtil;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
public class ArgInitialize {

    public static void handle(String[] args) throws IOException {
        Map<String, String> map = MainParamsUtil.read(args);
        //网络信息
        String ip = map.get(MustParamsKey.HOST);
        if (ip != null && ip.equals("0.0.0.0")) {
            RegisterConstant.HOST = InetAddress.getLocalHost().getHostAddress();
        } else {
            RegisterConstant.HOST = ip;
        }

        RegisterConstant.PORT = Integer.valueOf(map.get(MustParamsKey.PORT));
        RegisterConstant.ADDR = IpUtils.getAddr(RegisterConstant.HOST, RegisterConstant.PORT);
        //集群
        RegisterConstant.GROUP_CLUSTER = map.get(MustParamsKey.GROUP_CLUSTER);
        RegisterConstant.GROUP_NAME = map.get(MustParamsKey.GROUP_NAME);

        //JRaft
        RegisterConstant.ELECTION_TIMEOUT = Long.valueOf(map.get(MustParamsKey.ELECTION_TIMEOUT));

        //缓存文件、日志等路径
        RegisterConstant.LOG_DISK = map.get(MustParamsKey.LOG_DISK);
        RegisterConstant.RAFT_LOG_DISK = Paths.get(RegisterConstant.LOG_DISK, "raft").toString();
        RegisterConstant.TMP_LOG_DISK = Paths.get(RegisterConstant.LOG_DISK, "temp").toString();

        //分区路径
        RegisterConstant.PARTITION_DISK = map.get(MustParamsKey.PARTITION_DISK).split(",");

        //注册中心
        RegisterConstant.NACOS_HOST = map.get(MustParamsKey.NACOS_HOST);
        RegisterConstant.NACOS_PORT = Integer.valueOf(map.get(MustParamsKey.NACOS_PORT));
        RegisterConstant.NACOS_ADDR = IpUtils.getAddr(RegisterConstant.NACOS_HOST, RegisterConstant.NACOS_PORT);

        //leveldb
        RegisterConstant.LEVEL_DB = Paths.get(RegisterConstant.LOG_DISK, "leveldb").toString();

        //rs
        RegisterConstant.DATA_SHARDS = Integer.valueOf(map.get(MustParamsKey.DATA_SHARDS));
        RegisterConstant.PARITY_SHARDS = Integer.valueOf(map.get(MustParamsKey.PARITY_SHARDS));
        RsParam.instance = new RsParam(RegisterConstant.DATA_SHARDS, RegisterConstant.PARITY_SHARDS);
        RegisterConstant.TOTAL_SHARDS = RegisterConstant.DATA_SHARDS + RegisterConstant.PARITY_SHARDS;
        log.debug("RS data_shards:"+RegisterConstant.DATA_SHARDS);
        log.debug("RS parity_shards:"+RegisterConstant.PARITY_SHARDS);

        //负载均衡
        RegisterConstant.WIGHT = Double.valueOf(map.get(MustParamsKey.WIGHT));

        //encrypt
        if(map.get(MustParamsKey.ENCRYPT)!=null){
            RegisterConstant.ENCRYPT = EncryptionEnum.getEnum(map.get(MustParamsKey.ENCRYPT));
            log.debug("encrypt:"+RegisterConstant.ENCRYPT);
        }

        //清空
        if (map.get(MustParamsKey.CLEAR_BEFORE_RUNNING)!=null &&
                Boolean.parseBoolean(map.get(MustParamsKey.CLEAR_BEFORE_RUNNING))){
            FileUtils.forceDelete(new File(RegisterConstant.LOG_DISK));
            for (String pat : RegisterConstant.PARTITION_DISK) FileUtils.forceDelete(new File(pat));
            log.info("clear disk is ok.");
        }

        log.debug("param init success.");
    }
}
