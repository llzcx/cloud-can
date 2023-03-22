package ccw.serviceinnovation.common.util.http;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
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

}

