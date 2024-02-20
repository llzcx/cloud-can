package ccw.serviceinnovation;

import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.node.server.db.StorageEngine;
import ccw.serviceinnovation.node.util.PrintUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OssNodeApplication {
    public static void main(String[] args) throws Exception {
        StorageEngine.start(args);
        PrintUtils.log3D();
        log.info("Cluster:"+ RegisterConstant.GROUP_CLUSTER);
    }
}