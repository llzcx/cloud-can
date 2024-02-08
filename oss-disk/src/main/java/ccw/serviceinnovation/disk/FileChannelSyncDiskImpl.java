package ccw.serviceinnovation.disk;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * NIO FileChannel
 */
public class FileChannelSyncDiskImpl implements SyncDisk {
    public FileChannelSyncDiskImpl() throws IOException {
    }

    @Override
    public void initialize() throws IOException {
        // Implement initialization logic here
    }

    @Override
    public void save(Path path, byte[] bytes, int start, int size) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes, start, size);
            channel.write(buffer);
        }
    }

    @Override
    public byte[] read(Path path, int start, int size) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
            ByteBuffer buffer = ByteBuffer.allocate(size);
            channel.read(buffer, start);
            return buffer.array();
        }
    }

    @Override
    public void updateName(Path oldName, String newName) throws IOException {
        Files.move(oldName, oldName.resolveSibling(newName));
    }

    @Override
    public void del(Path path) throws IOException {
        Files.delete(path);
    }

}
