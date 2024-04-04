package ccw.serviceinnovation.disk;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;

public abstract class Data {

    protected Path path;

    public abstract long write(ByteBuffer buffer) throws IOException;

    public abstract int read(ByteBuffer dst, long position, int length) throws IOException;

    public abstract void close() throws IOException;

    public abstract long size() throws IOException;

    public abstract void delete() throws IOException;

    public abstract void force() throws IOException;

    protected Path path() {
        return path;
    }


    public static void main(String[] args)throws IOException  {
        ByteBuffer buffer1 = ByteBuffer.allocateDirect(100);
        buffer1.putInt(100);
        buffer1.putInt(200);
        buffer1.putInt(300);
        buffer1.putInt(400);
        buffer1.putInt(500);


        ByteBuffer buffer2 = ByteBuffer.allocateDirect(4);
        buffer1.position(4 * 2);
        buffer1.limit(4 * 2 + 4);
        buffer2.put(buffer1);
        buffer2.flip();
        System.out.println(buffer2.getInt());
    }
}