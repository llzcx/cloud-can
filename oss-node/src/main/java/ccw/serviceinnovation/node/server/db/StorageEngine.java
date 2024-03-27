package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.disk.FileChannelSyncDiskImpl;
import ccw.serviceinnovation.disk.SyncDisk;
import ccw.serviceinnovation.node.calculate.ByteHandler;
import ccw.serviceinnovation.node.calculate.EncryptAndSplitByteHandlerImpl;
import ccw.serviceinnovation.node.index.Index;
import ccw.serviceinnovation.node.index.IndexContext;
import ccw.serviceinnovation.node.index.LevelDbIndexImpl;
import ccw.serviceinnovation.node.partition.PartitionSelector;
import ccw.serviceinnovation.node.partition.PartitionSelectorImpl;
import ccw.serviceinnovation.node.server.constant.ArgInitialize;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.node.server.nacos.NacosConfig;
import com.alibaba.nacos.api.exception.NacosException;
import com.alipay.sofa.jraft.util.NamedThreadFactory;
import com.alipay.sofa.jraft.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;

import static ccw.serviceinnovation.node.server.constant.RegisterConstant.PARTITION_DISK;


@Slf4j
public class StorageEngine {
    private StorageEngine(){}
    /**
     * 状态机实现
     */
    public static ServiceHandler serviceHandler;
    /**
     * 注册中心
     */
    public static NacosConfig register;

    /**
     * 索引实现
     */
    public static Index index;

    /**
     * 持久化
     */
    public static SyncDisk syncDisk;

    /**
     * 字节流处理
     */
    public static ByteHandler<byte[]> byteHandler;

    public static PartitionSelector partitionSelector;



    public static void start(String[] args) throws IOException, NacosException {

        /*-----------------文件夹、变量-------------------*/
        //参数初始化
        ArgInitialize.handle(args);

        //文件路径
        FileUtils.forceMkdir(new File(RegisterConstant.LOG_DISK));
        FileUtils.forceMkdir(new File(RegisterConstant.RAFT_LOG_DISK));
        FileUtils.forceMkdir(new File(RegisterConstant.TMP_LOG_DISK));
        for (String path : RegisterConstant.PARTITION_DISK) {
            FileUtils.forceMkdir(new File(path));
        }

        /*-----------------索引、磁盘、字节处理、磁盘分区选择器-------------------*/

        //应用状态机实现
        serviceHandler = new ServiceHandlerImpl();
        serviceHandler.initialize();

        //磁盘
        syncDisk = new FileChannelSyncDiskImpl();
        syncDisk.initialize();

        //字节流处理
        byteHandler = new EncryptAndSplitByteHandlerImpl();
        byteHandler.initialize();

        //索引
        index = new LevelDbIndexImpl();
        IndexContext.index = index;
        index.load();

        //磁盘分区选择器
        partitionSelector = new PartitionSelectorImpl(PARTITION_DISK);


        /*-----------------服务注册-------------------*/
        //连接到注册中心
        register = new NacosConfig();
        register.connect();

        //启动raft服务
        DataServer.start();
    }
}
