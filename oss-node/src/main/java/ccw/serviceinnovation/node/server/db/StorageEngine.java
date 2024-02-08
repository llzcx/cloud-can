package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.node.server.constant.ArgInitialize;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.node.server.http.HttpServer;
import ccw.serviceinnovation.node.server.nacos.NacosConfig;
import com.alibaba.nacos.api.exception.NacosException;
import com.alipay.sofa.jraft.rhea.util.concurrent.NamedThreadFactory;
import com.alipay.sofa.jraft.util.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;


@Slf4j
public class StorageEngine {
    private static ThreadPoolExecutor executor   = ThreadPoolUtil
            .newBuilder()
            .poolName("STORAGE_ENGINE_EXECUTOR")
            .enableMetric(true)
            .coreThreads(3)
            .maximumThreads(5)
            .keepAliveSeconds(60L)
            .workQueue(new SynchronousQueue<>())
            .threadFactory(
                    new NamedThreadFactory("JRaft-Executor-", true)).build();
    private StorageEngine(){}
    /**
     * 状态机实现
     */
    public static OnApply onApply;
    /**
     * 注册中心
     */
    public static NacosConfig register;



    public static void start(String[] args) throws IOException, NacosException {
        //参数初始化
        ArgInitialize.handle(args);

        //文件路径
        FileUtils.forceMkdir(new File(RegisterConstant.LOG_DISK));
        FileUtils.forceMkdir(new File(RegisterConstant.RAFT_LOG_DISK));
        FileUtils.forceMkdir(new File(RegisterConstant.TMP_LOG_DISK));
        for (String path : RegisterConstant.PARTITION_DISK) {
            FileUtils.forceMkdir(new File(path));
        }


        //应用状态机实现
        onApply = new OnApplyImpl();
        onApply.initialize();

        //开启http访问服务
        executor.submit(HttpServer::start);


        //连接到注册中心
        register = new NacosConfig();
        register.connect();

        //启动raft服务
        DataServer.start();
    }
}
