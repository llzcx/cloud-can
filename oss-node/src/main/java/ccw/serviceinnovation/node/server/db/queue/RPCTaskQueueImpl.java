package ccw.serviceinnovation.node.server.db.queue;

import ccw.serviceinnovation.hash.select.HashCodeSelectorImpl;
import ccw.serviceinnovation.hash.select.ItemSelector;
import ccw.serviceinnovation.node.util.FutureUtil;
import com.alipay.sofa.jraft.util.NamedThreadFactory;
import com.alipay.sofa.jraft.util.ThreadPoolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class RPCTaskQueueImpl implements HandlerQueue {
    private final ItemSelector<ThreadPoolExecutor> threadPoolExecutorItemSelector;

    public RPCTaskQueueImpl(String handlerName, int consumer) {
        String upperCase = handlerName.toUpperCase();
        String threadNamePrefix = upperCase + "-Executor-";
        List<ThreadPoolExecutor> handlerList = new ArrayList<>(consumer);
        for (int i = 0; i < consumer; i++) {
            ThreadPoolExecutor executor = ThreadPoolUtil
                    .newBuilder()
                    .poolName(handlerName.toUpperCase() + "_ENGINE_EXECUTOR")
                    .enableMetric(true)
                    .coreThreads(1)
                    .maximumThreads(1)
                    .keepAliveSeconds(60L)
                    .workQueue(new LinkedBlockingQueue<>())
                    .threadFactory(new NamedThreadFactory(threadNamePrefix + i, true)).build();
            handlerList.add(executor);
        }
        threadPoolExecutorItemSelector = new HashCodeSelectorImpl<>(handlerList);
    }

    @Override
    public Future<?> submit(RPCTask rpcTask) {
        return threadPoolExecutorItemSelector.select(rpcTask.getKey()).submit(rpcTask);
    }
}
