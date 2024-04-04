package ccw.serviceinnovation.disk;

import ccw.serviceinnovation.util.Util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DiskData extends Data {

    private final FileChannel channel;

    private long readPosition;

    private long writePosition;

    private final ByteBuffer writeBuffer;

    private final ReentrantReadWriteLock lock;

    public DiskData(Path path, Integer bufferSize, OpenOption... options) throws IOException {
        this.path = path;
        if (!Files.exists(path)) Files.createFile(path);
        this.channel = FileChannel.open(path, options);
        this.readPosition = this.channel.size();
        this.writePosition = this.readPosition;
        this.writeBuffer = ByteBuffer.allocate(bufferSize);
        this.lock = new ReentrantReadWriteLock();
    }

    public long write(ByteBuffer buffer) throws IOException {
        try {
            this.lock.writeLock().lock();
            long prev = this.readPosition;
            if (this.writeBuffer.remaining() < buffer.remaining()) {
                this.writeBuffer.flip();
                this.writePosition += this.writeBuffer.remaining();
                this.channel.write(this.writeBuffer);
                this.writeBuffer.clear();
            }
            this.readPosition += buffer.remaining();
            this.writeBuffer.put(buffer);
            return prev;
        } finally {
            this.lock.writeLock().unlock();
        }
    }

    public int read(ByteBuffer dst, long position, int length) throws IOException {
        try {
            this.lock.readLock().lock();
            dst.limit(length);
            if (this.writePosition > position) {
                return this.channel.read(dst, position);
            } else if (this.readPosition > position) {
                int recPos = this.writeBuffer.position();
                int recLit = this.writeBuffer.limit();
                this.writeBuffer.position((int) (position - this.writePosition));
                this.writeBuffer.limit(dst.limit());
                dst.put(this.writeBuffer);
                dst.position(dst.limit());
                this.writeBuffer.position(recPos);
                this.writeBuffer.limit(recLit);
                return length;
            }
            return 0;
        } finally {
            this.lock.readLock().unlock();
        }
    }

    public void close() throws IOException {
        this.channel.close();
    }

    public long size() throws IOException {
        return Files.size(this.path);
    }

    public void delete() throws IOException {
        this.channel.close();
        Files.deleteIfExists(this.path);
    }

    public void move(Path newPath) throws IOException {
        Files.move(path, newPath);
    }

    public void force() throws IOException {
        this.lock.writeLock().lock();
        this.writeBuffer.flip();
        this.writePosition += this.writeBuffer.remaining();
        if (this.writeBuffer.remaining() > 0) this.channel.write(this.writeBuffer);
        this.channel.force(false);
        this.writeBuffer.clear();
        this.lock.writeLock().unlock();
    }

    static int INT_SIZE = 4;

    public static void main(String[] args) throws IOException {
        Path path1 = Paths.get("D:\\oss\\ABC");
        Files.delete(path1);

        ByteBuffer buffer = ByteBuffer.wrap(new byte[100]);
        System.out.println("buffer:"+buffer.remaining());

        Data data = new DiskData(path1, INT_SIZE * 10, StandardOpenOption.READ, StandardOpenOption.WRITE);
        int intTotal = 120;
        for (int i = 1; i <= intTotal; i++) {
            ByteBuffer allocate = ByteBuffer.allocate(INT_SIZE);
            allocate.putInt(i * 100);
            allocate.flip();
            System.out.println("allocate.remaining:"+allocate.remaining());
            data.write(allocate);
        }
        ByteBuffer intArr = ByteBuffer.allocate(INT_SIZE);
        data.read(intArr, INT_SIZE * (12L - 1), INT_SIZE);
        intArr.flip();
        System.out.println(intArr.getInt());
    }
}
