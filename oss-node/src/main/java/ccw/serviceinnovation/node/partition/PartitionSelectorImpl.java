package ccw.serviceinnovation.node.partition;

import ccw.serviceinnovation.hash.select.HashCodeSelectorImpl;
import ccw.serviceinnovation.hash.select.ItemSelector;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 磁盘选择器实现：hashcode取余
 */
public class PartitionSelectorImpl implements PartitionSelector {

    ItemSelector<String> itemSelector;

    public PartitionSelectorImpl(String[] disks) {
        this.itemSelector = new HashCodeSelectorImpl<>(new ArrayList<>(Arrays.asList(disks)));
    }

    @Override
    public Path get(String objectKey, int index) {
        String disk = itemSelector.select(objectKey + index);
        return Paths.get(disk);
    }

}
