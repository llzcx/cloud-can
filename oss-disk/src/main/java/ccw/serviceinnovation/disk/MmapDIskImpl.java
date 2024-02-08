package ccw.serviceinnovation.disk;

import ccw.serviceinnovation.util.MMapUtil;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * mmap
 */
public class MmapDIskImpl implements SyncDisk {
    @Override
    public void initialize() throws IOException {
        // Implement initialization logic here
    }

    @Override
    public void save(Path path, byte[] bytes, int start, int size) throws IOException {
        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, bytes.length);
            buffer.put(bytes, start, size);
            MMapUtil.unmap(buffer);
        }
    }

    @Override
    public byte[] read(Path path, int start, int size) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
            MappedByteBuffer buffer = raf.getChannel().map(FileChannel.MapMode.READ_ONLY, start, size);
            byte[] data = new byte[size];
            buffer.get(data);
            return data;
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
