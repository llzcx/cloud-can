package ccw.serviceinnovation.node.server.db;

import java.util.ArrayList;
import java.util.List;

public class ShutDownHook {
    private static final List<Runnable> list = new ArrayList<>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          System.out.println("Cloud Can Server Terminating ...");
            for (Runnable runnable : list) {
                runnable.run();
            }
        }));
    }

    public static void add(Runnable runnable) {
        list.add(runnable);
    }
} 
