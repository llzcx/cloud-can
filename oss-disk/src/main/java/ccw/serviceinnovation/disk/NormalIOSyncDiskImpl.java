package ccw.serviceinnovation.disk;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 普通IO
 */
public class NormalIOSyncDiskImpl extends SyncDisk {
    @Override
    public void initialize() throws IOException {

    }

    @Override
    public void save(Path path, long begin, byte[] buffer, int start, int size) throws IOException {

    }

    @Override
    public byte[] read(Path path, long start, int size, byte[] buffer) throws IOException {
        return null;
    }
}
