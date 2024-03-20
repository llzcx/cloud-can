package ccw.serviceinnovation.node.util;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureUtil {
    public static void await(List<Future<?>> list) throws ExecutionException, InterruptedException {
        for (Future<?> future : list) future.get();
    }
}
