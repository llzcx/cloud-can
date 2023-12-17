package ccw.serviceinnovation.oss.manager.test;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static void createCSV(String path,String csvPath){
        // 保存MD5值的TXT文件
        String md5File = csvPath;
        File md5 = new File( md5File);

        try (FileOutputStream fos = new FileOutputStream(md5)) {
            if (!md5.exists()) {
                md5.createNewFile();
            }

            File dir = new File(path);
            File[] files = dir.listFiles();

            if (files != null && files.length > 0) {
                for (File file : files) {
                    // 判断是否是文件
                    if (file.isFile()) {
                        // 文件名
                        String fileName = file.getName();
                        // 文件路径
                        String filePath = file.getAbsolutePath();
                        // 获取文件MD5值
                        String md5Value = getFileMd5(file);
                        // 拼接文件路径、文件名和MD5值，以逗号分隔
                        String content = filePath + "," + fileName + "," + md5Value + "\n";
                        // 将内容写入TXT文件
                        fos.write(content.getBytes());
                    }
                }
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        System.out.println("生成完成！");
    }
    private static String getFileMd5(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");

        byte[] buffer = new byte[1024 * 1024];
        int len;

        try (FileInputStream fis = new FileInputStream(file)) {
            while ((len = fis.read(buffer)) != -1) {
                md5.update(buffer, 0, len);
            }
        }

        byte[] digest = md5.digest();
        StringBuilder sb = new StringBuilder();

        for (byte b : digest) {
            // 将字节转换为16进制字符串
            String s = Integer.toHexString(b & 0xff);
            if (s.length() == 1) {
                sb.append("0");
            }
            sb.append(s);
        }

        return sb.toString();
    }

    public static void generateUserData(String filePath, int numUsers) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            Random random = new Random();
            for (int i = 0; i < numUsers; i++) {
                String username = "";
                String password = "";
                for (int j = 0; j < 5; j++) {
                    username += (char) (random.nextInt(26) + 97);
                    password += random.nextInt(10);
                }
                String userData = username + "," + password + "\n";
                fileWriter.write(userData);
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void generateBucket(String filePath, int numUsers) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            Random random = new Random();
            for (int i = 0; i < numUsers; i++) {
                String username = "";
                String password = "";
                for (int j = 0; j < 5; j++) {
                    username += (char) (random.nextInt(26) + 97);
                }
                String userData = username + "," + 1 + "\n";
                fileWriter.write(userData);
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String path = "D:\\OSS\\testdata";
        String csvPath = "D:\\OSS\\csv.txt";
//        handle(path,256 * 1024,10000);
//        delete(path,10000);
//        System.out.println(1);
//        createCSV(path,csvPath);
        generateBucket("D:\\OSS\\testbucket.txt",1000);
    }

}
