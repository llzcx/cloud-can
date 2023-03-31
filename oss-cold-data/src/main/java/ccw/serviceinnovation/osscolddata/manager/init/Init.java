package ccw.serviceinnovation.osscolddata.manager.init;

import ccw.serviceinnovation.osscolddata.controller.OssColdController;

import java.io.File;

import static ccw.serviceinnovation.osscolddata.constant.FilePrefixConstant.FILE_COLD;
import static ccw.serviceinnovation.osscolddata.constant.OssColdDataConstant.POSITION;
/**
 * @author 陈翔
 */
public class Init {

    public static void initFileKey() {
        System.out.println("position:"+ POSITION);
        File fileDir = new File(POSITION);
        File[] files = fileDir.listFiles();
        for (File file : files) {
            String name = file.getName();
            if(name.startsWith(FILE_COLD)){
                String etag = name.substring(FILE_COLD.length());
                System.out.println("加入了etag:"+etag);
                OssColdController.data.put(etag,file.getAbsolutePath());
            }
        }
    }

    public static void fileInit() {

    }


}
