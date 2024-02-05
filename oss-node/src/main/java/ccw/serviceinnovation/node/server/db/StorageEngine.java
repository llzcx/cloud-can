package ccw.serviceinnovation.node.server.db;

import ccw.serviceinnovation.node.disk.Disk;
import ccw.serviceinnovation.node.disk.DiskImpl;
import ccw.serviceinnovation.node.index.EtagIndexHashMapImpl;
import ccw.serviceinnovation.node.index.Index;
import ccw.serviceinnovation.node.server.constant.ArgInitialize;
import ccw.serviceinnovation.node.server.http.HttpServer;
import ccw.serviceinnovation.node.server.nacos.NacosConfig;
import com.alibaba.nacos.api.exception.NacosException;
import com.alipay.sofa.jraft.rhea.util.concurrent.NamedThreadFactory;
import com.alipay.sofa.jraft.util.ThreadPoolUtil;

import java.io.IOException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;

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
                    new NamedThreadFactory("JRaft-Test-Executor-", true)).build();
    private StorageEngine(){}

    /**
     * 索引实现
     */
    public static Index index;
    /**
     * 状态机实现
     */
    public static OnApply onApply;
    /**
     * 注册中心
     */
    private static NacosConfig register;
    /**
     * 持久化
     */
    private static Disk disk;







    public static void start(String[] args) throws IOException, NacosException {
        //参数初始化
        ArgInitialize.handle(args);

        //文件路径
        disk = new DiskImpl();
        disk.initialize();

        //索引
        index = new EtagIndexHashMapImpl();
        index.load();

        //应用状态机实现
        onApply = new OnApplyImpl();

        //开启http访问服务
        executor.submit(HttpServer::start);

        //启动Jraft服务
        executor.submit(DataServer::start);


        //连接到Nacos
        register = new NacosConfig();
        register.connect();


    }
}
