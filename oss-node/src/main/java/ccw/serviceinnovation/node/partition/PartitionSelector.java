package ccw.serviceinnovation.node.partition;

import ccw.serviceinnovation.hash.select.ItemSelector;

import java.nio.file.Path;

/**
 * 磁盘分区选择器
 */
public interface PartitionSelector {

    Path get(String objectKey,int index);
}
