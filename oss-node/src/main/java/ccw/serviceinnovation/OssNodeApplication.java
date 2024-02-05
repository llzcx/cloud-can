package ccw.serviceinnovation;

import ccw.serviceinnovation.node.server.db.StorageEngine;
import org.slf4j.LoggerFactory;

public class OssNodeApplication {
    public static void main(String[] args) throws Exception {
        StorageEngine.start(args);
    }
}