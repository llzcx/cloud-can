package ccw.serviceinnovation.common.util.file;

import cn.hutool.core.img.Img;
import cn.hutool.core.io.FileUtil;

/**
 * @author 陈翔
 */
public class JpegCompress {

    public static boolean compress(String source,String target){
        Img.from(FileUtil.file(source))
                .setQuality(0.5)
                .write(FileUtil.file(target));
        return true;
    }

    public static void main(String[] args) {
        Img.from(FileUtil.file("D:\\OSS\\123l123123.jpg"))
                .setQuality(0.1)
                .write(FileUtil.file("D:\\OSS\\123l123123.jpg"));
    }

}
