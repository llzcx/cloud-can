package ccw.serviceinnovation.node.select;

import java.nio.file.Path;

/**
 * 磁盘分区选择器
 */
public interface PartitionSelector {

    Path get(String objectKey,int index);
}
