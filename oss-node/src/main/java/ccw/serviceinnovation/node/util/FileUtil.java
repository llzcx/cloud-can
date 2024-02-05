package ccw.serviceinnovation.node.util;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;



/**
 * 文件工具类
 * @author 陈翔
 */
public final class FileUtil {
    /**
     * 序列化保存
     * @param o
     * @param file
     */
    public static void saveToDisk(Object o, File file) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new GZIPOutputStream(new FileOutputStream(file)))) {
            oos.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("保存失败：" + e.getMessage());
        }
    }

    /**
     * 从磁盘中加载磁盘数据
     * @param file
     * @param cls
     * @param <T>
     * @return
     */
    public static <T> T loadFromDisk(File file,Class<T> cls) {
        T o = null;
        try (ObjectInputStream ois = new ObjectInputStream(
                new GZIPInputStream(new FileInputStream(file)))) {
            o = (T) ois.readObject();
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            e.printStackTrace();
            System.out.println("加载失败：" + e.getMessage());
        }
        return o;
    }

    /**
     * 删除文件夹当中所有数据
     * @param dir
     * @param deleteDirItself
     * @return
     */
    public static boolean cleanDir(File dir, boolean deleteDirItself) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            assert children != null;
            for (String child : children) {
                boolean ret = cleanDir(new File(dir, child), true);
                if (!ret) {
                    return false;
                }
            }
        }
        if (deleteDirItself) {
            return dir.delete();
        }
        return true;
    }

    public static String readFileToString(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contentBuilder.toString();
    }


    public static boolean checkBuffer(ByteBuffer buffer,long size){
        return buffer != null && buffer.remaining() >= size;
    }


    /**
     * 将buffer里的数据写入channel
     * @param channel
     * @param buffer
     * @throws IOException
     */
    public static void flush(FileChannel channel,ByteBuffer buffer) throws IOException{
        buffer.flip();
        final MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, channel.size(), buffer.remaining());
        map.put(buffer);

        buffer.clear();
    }
}
