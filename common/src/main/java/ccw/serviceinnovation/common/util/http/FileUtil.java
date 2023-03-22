package ccw.serviceinnovation.common.util.http;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;

/**
 * @author FeianLing
 * @date 2019/9/16
 */
public class FileUtil {
    private FileUtil() {
        //ignore this method
    }

    /**
     * @param
     * @param fileUrl
     * @param filePath
     * @return void
     * @author FeianLing
     * @date 2019/9/16
     * @desc 通过url请求将文件保存到本地
     */
    public static boolean saveFile(String fileUrl, String filePath) throws IOException {
        URL url = new URL(fileUrl);
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileUtils.copyURLToFile(url, file);
        return true;
    }

    //使用lastIndexOf()结合subString()获取后缀名
    public static String lastName(String filename){
        if(filename.lastIndexOf(".")==-1){
            return "";//文件没有后缀名的情况
        }
        //此时返回的是带有 . 的后缀名，
        return filename.substring(filename.lastIndexOf("."));
    }

    public static void main(String[] args) throws IOException {
        String s = "D:\\oss\\1.jpg";
        System.out.println(lastName(s));
    }

    /**
     * 方法功能：读取文件内容返回字节流
     *
     * @param fname
     * @return byte[]
     */

    public static byte[] readFile(String fname) {
        InputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            fis = new FileInputStream(fname);
            byte[] ch = new byte[1024];
            int readLen = 0;
            while ((readLen = fis.read(ch)) != -1) {
                baos.write(ch, 0, readLen);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
            }
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            System.out.println("文件删除失败,请检查文件路径是否正确");
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            String name = file.getName();
            System.out.println(name);
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
    }
}

