package ccw.serviceinnovation.node.server.constant;

import ccw.serviceinnvation.encryption.consant.EncryptionEnum;

public class RegisterConstant {
    /**
     * 本机IP（自动获取）
     */
    public static String HOST;
    /**
     * 服务端口
     */
    public static Integer PORT;
    /**
     * 本地服务地址：ip:port
     */
    public static String ADDR;
    /**
     * 服务组名称
     */
    public static String GROUP_NAME;
    /**
     * Raft服务列表
     */
    public static String GROUP_CLUSTER;

    public static Long ELECTION_TIMEOUT;

    /**
     * 存储目录
     */
    public static String LOG_DISK;

    /**
     * Raft存储目录（在存储目录之下，自动获取）
     */
    public static String RAFT_LOG_DISK;
    /**
     * 分片上传缓存目录（在存储目录之下，自动获取）
     */
    public static String TMP_LOG_DISK;
    /**
     * 磁盘路径，根据RS算法会分磁盘进行保存
     */
    public static String[] PARTITION_DISK;

    /**
     * Redis服务地址
     */
    public static String LEVEL_DB;

    /**
     * Nacos服务IP
     */
    public static String NACOS_HOST;

    /**
     * Nacos服务端口
     */
    public static Integer NACOS_PORT;

    /**
     * nacos服务地址（自动获取）
     */
    public static String NACOS_ADDR;
    /**
     * 数据片个数
     */
    public static Integer DATA_SHARDS;
    /**
     * 校验片个数
     */
    public static Integer PARITY_SHARDS;
    /**
     * 总数量，自动获取
     */
    public static Integer TOTAL_SHARDS;
    /**
     * 加密
     */
    public static EncryptionEnum ENCRYPT;

    public static Double WIGHT;
}
