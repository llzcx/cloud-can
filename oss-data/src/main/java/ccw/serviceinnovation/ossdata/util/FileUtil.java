package ccw.serviceinnovation.ossdata.util;

import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈翔
 */
public class FileUtil {

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

    public static void main(String[] args) {
        Map<String,String> mp = new HashMap<>();
        mp.put("group", "group1");
        System.out.println(JSONObject.toJSONString(mp));

    }
}
