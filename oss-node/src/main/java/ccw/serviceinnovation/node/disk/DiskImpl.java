package ccw.serviceinnovation.node.disk;

import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class DiskImpl implements Disk{
    public DiskImpl() throws IOException {

    }

    @Override
    public void initialize() throws IOException {
        FileUtils.forceMkdir(new File(RegisterConstant.LOG_DISK));
        FileUtils.forceMkdir(new File(RegisterConstant.RAFT_LOG_DISK));
        FileUtils.forceMkdir(new File(RegisterConstant.TMP_LOG_DISK));
        for (String path : RegisterConstant.PARTITION_DISK) {
            FileUtils.forceMkdir(new File(path));
        }
    }
}
