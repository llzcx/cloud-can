package ccw.serviceinnovation.oss.manager.test;

import ccw.serviceinnovation.hash.directcalculator.EtagDirectCalculator;
import ccw.serviceinnovation.hash.directcalculator.MD5EtagDirectCalculatorAdapter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

/**
 * @author 陈翔
 */
public class CreateTestData {

    public static EtagDirectCalculator etagFactory() {
        return new MD5EtagDirectCalculatorAdapter();
    }

    /**
     * 生成随机文件
     * @param path     生成的路径
     * @param fileSize 单个文件的大小
     * @param fileNum  文件数量
     */
    public static void toFile(String path, Integer fileSize, Integer fileNum) throws IOException {
        if (!new File(path).exists()) Files.createDirectories(Paths.get(path));
        for (int i = 1; i <= fileNum; i++) {
            File file = new File(path + "\\" + "object" + i);
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                raf.setLength(fileSize);
                byte[] buffer = new byte[fileSize];
                new Random().nextBytes(buffer);
                raf.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("ok. NUM:" + fileNum + ",SIZE:" + fileSize + ",+DATA_PATH:" + path);
    }

    /**
     * 文件路径,文件名,HASH
     *
     * @param path
     * @param csvPath
     */
    public static void createCSV(String path, String csvPath) {
        // 保存MD5值的TXT文件
        File md5 = new File(csvPath);
        try (FileOutputStream fos = new FileOutputStream(md5)) {
            if (!md5.exists() && md5.createNewFile()) ;
            File dir = new File(path);
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        String fileName = file.getName();
                        String filePath = file.getAbsolutePath();
                        String md5Value = etagFactory().get(Paths.get(filePath));
                        String content = filePath + "," + fileName + "," + md5Value + "\n";
                        fos.write(content.getBytes());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ok. CSV_PATH:" + csvPath);
    }
    public static void deleteFilesInFolder(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            } else {
                System.out.println("Folder is empty or cannot be accessed.");
            }
        } else {
            folder.mkdirs();
        }
    }
    public static void main(String[] args) throws IOException {
        String path = "D:\\OSS\\testdata";
        String csvPath = "D:\\OSS\\csv.txt";
        deleteFilesInFolder(path);
        if (!new File(csvPath).exists() || new File(csvPath).delete()) {
            toFile(path, 256 * 1024, 2000);
            createCSV(path, csvPath);;
        }

    }
}
