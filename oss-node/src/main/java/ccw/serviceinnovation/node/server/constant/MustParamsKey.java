package ccw.serviceinnovation.node.server.constant;

public interface MustParamsKey {
    String HOST = "host";
    String PORT = "port";
    String GROUP_NAME = "group_name";

    String GROUP_CLUSTER = "group_cluster";

    String ELECTION_TIMEOUT =  "election_timeout";

    String REDIS_HOST = "redis_host";

    String REDIS_PORT = "redis_port";

    String LOG_DISK = "log_disk";
    String PARTITION_DISK = "partition_disk";

    String NACOS_HOST = "nacos_host";

    String NACOS_PORT = "nacos_port";

    String DATA_SHARDS = "data_shards";

    String PARITY_SHARDS = "parity_shards";
}
