package ccw.serviceinnovation;

import ccw.serviceinnovation.node.server.db.StorageEngine;

public class OssNodeApplication {
    public static void main(String[] args) throws Exception {
        StorageEngine.start(args);
    }
}