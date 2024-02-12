package ccw.serviceinnovation.node.util;

import java.lang.management.ManagementFactory;

public class ProcessUtil {
    public static long getId(){
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        // Extracting PID from the JVM name
        return Long.parseLong(jvmName.split("@")[0]);
    }
}
