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

    public static void main(String[] args) throws IOException {
        String fileUrl = "https://blog.csdn.net/lingfeian/article/details/100052895";
        String filePath = "D:/mnt/uploads/tmp/test.pdf";
        String jdkUrl = "https://download.oracle.com/java/19/latest/jdk-19_linux-x64_bin.deb ( sha256)";

        String img = "https://img-blog.csdnimg.cn/20190317165625774.?x-oss-proces" +
                "s=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl8zNzYwMTU0Ng==,size_16,color_FFFFFF,t_70";
        String path = "D:\\OSS\\1232.jpg";
        FileUtil.saveFile(img, path);
    }

}

