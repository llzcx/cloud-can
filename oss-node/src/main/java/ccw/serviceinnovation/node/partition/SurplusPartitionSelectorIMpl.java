package ccw.serviceinnovation.node.partition;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 取余选择器
 */
public class SurplusPartitionSelectorIMpl implements PartitionSelector {


    private final String[] disks;
    private final int count;

    public SurplusPartitionSelectorIMpl(String[] disks) {
        this.disks = disks;
        this.count = disks.length;
    }

    @Override
    public Path get(String objectKey, int index) {
        String disk = disks[index % count];
        return Paths.get(disk);
    }
}
