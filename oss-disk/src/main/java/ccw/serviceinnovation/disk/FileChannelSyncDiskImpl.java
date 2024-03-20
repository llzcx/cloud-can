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
public class FileChannelSyncDiskImpl extends SyncDisk {
    public FileChannelSyncDiskImpl() throws IOException {
    }

    public void initialize() throws IOException {
        // 初始化逻辑，例如打开文件通道等
        // 此处略
    }

    public void save(Path path, long begin, byte[] buffer, int start, int size) throws IOException {
        // 将buffer中从start位置开始的size字节写入文件的begin位置处
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, start, size);
            fileChannel.write(byteBuffer, begin);
        }
    }

    public byte[] read(Path path, long start, int size, byte[] buffer) throws IOException {
        // 从文件的start位置处读取size字节到buffer中
        try (FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE)) {
            if (size == -1) {
                size = (int) fileChannel.size();
                buffer = new byte[size];
            }
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer, 0, size);
            fileChannel.read(byteBuffer, start);
        }
        return buffer;
    }


}
