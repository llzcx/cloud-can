package ccw.serviceinnovation.disk;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 磁盘操作接口
 */
public abstract class SyncDisk {
    public abstract void initialize() throws IOException;

    public abstract void save(Path path,long begin, byte[] buffer, int start, int size) throws IOException;

    public abstract byte[] read(Path path, long start,int size,byte[] buffer) throws IOException;

    public void updateName(Path oldName,String newName)throws IOException{
        Files.move(oldName, oldName.resolveSibling(newName));
    }

    public void del(Path path) throws IOException{
        if(exist(path)){
            Files.delete(path);
        }
    }

    public void truncate(Path path,long size){
        try {
            try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw")) {
                try (FileChannel channel = file.getChannel()) {
                    channel.truncate(size);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setSize(Path path,long size){
        try {
            try (RandomAccessFile file = new RandomAccessFile(path.toFile(), "rw")) {
                file.setLength(size);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean exist(Path path){
        return Files.exists(path);
    }
}
