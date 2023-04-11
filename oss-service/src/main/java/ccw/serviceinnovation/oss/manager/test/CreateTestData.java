package ccw.serviceinnovation.oss.manager.test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * @author 陈翔
 */
public class CreateTestData {
    /**
     *
     * @param path 生成的路径
     * @param fileSize 单个文件的大小
     * @param fileNum 文件数量
     */
    public static void handle(String path,Integer fileSize,Integer fileNum){
        // 循环生成文件
        for (int i = 1; i <= fileNum; i++) {
            // 文件名称
            String fileName = "file" + i + ".dat";
            File file = new File(path + "\\" + fileName);
            // 设置文件大小
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                raf.setLength(fileSize);
                // 生成随机内容
                byte[] buffer = new byte[fileSize];
                new Random().nextBytes(buffer);
                // 写入文件
                raf.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("生成完成！");
    }

    public static void delete(String path,Integer fileNum){
        for (int i = 1; i <= fileNum; i++) {
            // 文件名称
            String fileName = "file" + i + ".dat";
            File file = new File(path + fileName);
            file.delete();
        }
    }

    public static void main(String[] args) {
        String path = "D:\\OSS\\testdata";
        handle(path,256 * 1024,10000);
//        delete(path,10000);
    }
}
