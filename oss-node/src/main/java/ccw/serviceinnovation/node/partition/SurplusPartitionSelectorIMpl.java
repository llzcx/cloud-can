package ccw.serviceinnovation.node.partition;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SurplusPartitionSelectorIMpl implements PartitionSelector {


    private final String[] disks;
    private final String prefix;

    private final String suffix;
    private final int count;

    public SurplusPartitionSelectorIMpl(String[] disks, String prefix, String suffix) {
        this.disks = disks;
        this.prefix = prefix;
        this.count = disks.length;
        this.suffix = suffix;
    }

    @Override
    public Path get(String objectKey, int index) {
        String disk = disks[index % count];
        return Paths.get(disk,prefix+objectKey+suffix+index);
    }
}
