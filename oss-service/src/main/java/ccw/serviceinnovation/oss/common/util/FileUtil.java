package ccw.serviceinnovation.oss.common.util;


import java.io.File;
import java.io.IOException;
import java.util.Locale;


/**
 * 文件操作工具类
 * @author 陈翔
 */
public class FileUtil {
    private static final  String[] IMAGE_TYPES ={
            "JPG",
            "JPEG",
            "TIFF",
            "PNG",
            "GIF",
            "PSD",
            "RAW",
            "PDF",
            "SVG",
            "EPS",
            "BMP",
    };

    private static final  String[] VIDEO_TYPES ={
            "AVI",
            "WMV",
            "MPG",
            "MPEG",
            "MP4",
            "MOV",
            "RM",
            "CreateRamUserDto",
            "SWF",
            "FLV",
    };
    /**
     * 创建文件夹
     * @param path
     * @param name
     * @return
     * @throws IOException
     */
    public static boolean createFolder(String path,String name) throws IOException{
        File file = new File(path+"/"+name);
        return file.mkdir();
    }

    /**
     * 判断是否为图片
     * @param type
     * @return
     */
    public static boolean isImage(String type){
        type = type.toUpperCase(Locale.ROOT);
        for (String s : IMAGE_TYPES) {
            if(s.equals(type)){
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为视频
     * @param type
     * @return
     */
    public static boolean isVideo(String type){
        type = type.toUpperCase(Locale.ROOT);
        for (String s : VIDEO_TYPES) {
            if(s.equals(type)){
                return true;
            }
        }
        return false;
    }



}
