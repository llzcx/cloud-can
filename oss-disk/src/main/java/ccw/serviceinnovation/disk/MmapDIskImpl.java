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
public class MmapDIskImpl extends SyncDisk {

    public void initialize() throws IOException {
        // 初始化逻辑，例如打开文件通道等
        // 此处略
    }

    public void save(Path path, long begin, byte[] buffer, int start, int size) throws IOException {
        // 将buffer中从start位置开始的size字节写入文件的begin位置处
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "rw");
             FileChannel fileChannel = randomAccessFile.getChannel()) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, begin, size);
            mappedByteBuffer.put(buffer, start, size);
            MMapUtil.unmap(mappedByteBuffer);
        }
    }

    public byte[] read(Path path, long start, int size,byte[] buffer) throws IOException {
        // 从文件的start位置处读取size字节到buffer中
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(path.toFile(), "r");
             FileChannel fileChannel = randomAccessFile.getChannel()) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, start, size);
            mappedByteBuffer.get(buffer, 0, size);
            MMapUtil.unmap(mappedByteBuffer);
        }
        return buffer;
    }
}
