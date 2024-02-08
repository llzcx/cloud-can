package ccw.serviceinnovation.node.partition;

import java.nio.file.Path;

public interface PartitionSelector {

    Path get(String objectKey,int index);
}
