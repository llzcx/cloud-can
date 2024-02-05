package ccw.serviceinnovation.node.server.constant;

import ccw.serviceinnovation.common.util.IpUtils;
import ccw.serviceinnovation.node.bo.RsParam;
import ccw.serviceinnovation.node.util.MainParamsUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
        RegisterConstant.NACOS_ADDR = "nacos://" + IpUtils.getAddr(RegisterConstant.NACOS_HOST, RegisterConstant.NACOS_PORT) + "?registry-type=service";

        //元数据信息
        RegisterConstant.REDIS_HOST = map.get(MustParamsKey.REDIS_HOST);
        RegisterConstant.REDIS_PORT = Integer.valueOf(map.get(MustParamsKey.REDIS_PORT));

        //rs
        RegisterConstant.DATA_SHARDS = Integer.valueOf(map.get(MustParamsKey.DATA_SHARDS));
        RegisterConstant.PARITY_SHARDS = Integer.valueOf(map.get(MustParamsKey.PARITY_SHARDS));
        RsParam.instance = new RsParam(RegisterConstant.DATA_SHARDS, RegisterConstant.PARITY_SHARDS);
        RegisterConstant.TOTAL_SHARDS = RegisterConstant.DATA_SHARDS + RegisterConstant.PARITY_SHARDS;

        //http
        RegisterConstant.HTTP_PORT = IpUtils.findAvailablePort();
        log.info("HTTP server port is "+RegisterConstant.HTTP_PORT);
    }
}
