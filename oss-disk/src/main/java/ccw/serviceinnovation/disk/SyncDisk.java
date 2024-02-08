package ccw.serviceinnovation.disk;

import java.io.IOException;
import java.nio.file.Path;

/**
 * 磁盘操作接口
 */
public interface SyncDisk {
    void initialize() throws IOException;

    void save(Path path, byte[] bytes, int start, int size) throws IOException;

    byte[] read(Path path, int start, int size) throws IOException;

    void updateName(Path oldName,String newName)throws IOException;

    void del(Path path) throws IOException;
}
