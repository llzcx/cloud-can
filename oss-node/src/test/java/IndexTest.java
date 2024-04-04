import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.index.Index;
import ccw.serviceinnovation.node.index.LevelDbIndexImpl;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.C;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ccw.serviceinnovation.node.server.constant.RegisterConstant.ENCRYPT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IndexTest {
    @Test
    public void test() throws IOException, InterruptedException {
        RegisterConstant.LEVEL_DB = "./level";
        Index index = new LevelDbIndexImpl();
        index.load();
        ExecutorService executor = Executors.newFixedThreadPool(8+1);
        String key = "123";
        index.add(key, new ObjectMeta(key, EncryptionEnum.SM4));
        int COUNT = 10000;
        CountDownLatch countDownLatch = new CountDownLatch(COUNT);
        for (int i = 0; i < COUNT; i++) {
            executor.execute(() -> {
                index.incr(key);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();

        // 关闭线程池
        executor.shutdown();
        ObjectMeta objectMeta = index.get(key);
        System.out.println(objectMeta.getCount());
        assertEquals((int) objectMeta.getCount(), COUNT + 1);

    }
}
