package ccw.serviceinnovation.osscolddata.controller;

import ccw.serviceinnovation.common.util.file.ZipUtil;
import ccw.serviceinnovation.common.util.http.FileUtil;
import ccw.serviceinnovation.osscolddata.constant.OssColdDataConstant;
import ccw.serviceinnovation.osscolddata.util.ControllerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static ccw.serviceinnovation.osscolddata.constant.FilePrefixConstant.FILE_COLD;
import static ccw.serviceinnovation.osscolddata.constant.FilePrefixConstant.FILE_COLD_TMP;

/**
 * @author 陈翔
 */

@RestController
@RequestMapping("/cold")
@Slf4j
public class OssColdController {

    public static ConcurrentHashMap<String,String> data = new ConcurrentHashMap<>();

    /**
     * oss-service调用此接口 将oss-data 数据归档 oss-cold-data
     * @param etag
     * @return
     */
    @GetMapping("/freeze/{ip}/{port}/{etag}")
    public void freeze(@PathVariable String etag, HttpServletResponse response, @PathVariable String ip, @PathVariable String port) throws Exception{
        //将文件下载
        String url = "http://" + ip + ":" + port + "/object/download/group/" + etag + "?name=1";
        String uuid = UUID.randomUUID().toString().replace("-", "_");
        String path = OssColdDataConstant.POSITION + "\\" + FILE_COLD_TMP + uuid;
        //以uuid为后缀保存
        boolean b = FileUtil.saveFile(url, path);
        log.info("从 {} 的保存结果:{}",url,b);
        File oldFile = new File(path);
        //保存为一个以etag为后缀的文件
        File newFile = new File(OssColdDataConstant.POSITION+"\\"+ FILE_COLD +etag);
        File[] files = new File[1];
        files[0] = oldFile;
        //压缩
        ZipUtil.archiveFiles2Zip(files,newFile,true);
        boolean delete = oldFile.delete();
        log.info("删除结果:{}",delete);
    }

    public static void main(String[] args) {
        File oldFile = new File("D:\\OSS\\test\\1NARAKA  BLADEPOINT 2023.02.16 - 03.23.26.28.DVR.1676489314875.mp4");
        File newFile = new File("D:\\OSS\\test\\2");
        File file = new File("D:\\OSS\\test1");
        File[] files = new File[1];
        files[0] = oldFile;
        ZipUtil.archiveFiles2Zip(files,newFile,true);
        try {
            String unzip = ZipUtil.unzip("D:\\OSS\\test\\2", "D:\\OSS\\test1");
            System.out.println(unzip);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * oss-data调用此接口 将oss-cold-data 数据解冻到 oss-data
     * @param etag
     * @return
     */
    @GetMapping("/unfreeze/{etag}")
    public String unfreeze(@PathVariable String etag) throws Exception{
        String path = OssColdDataConstant.POSITION + "\\" + FILE_COLD + etag;
        //将文件解压缩 解压会还原为原来的文件 这时候把uuid返回
        String tokenPath = ZipUtil.unzip(path, OssColdDataConstant.POSITION);
        //删除原来的文件
        boolean delete = new File(path).delete();
        return tokenPath;
    }

    /**
     * oss-data调用此接口 将oss-cold-data 数据解冻到 oss-data
     * @param token
     * @return
     */
    @GetMapping("/download/{token}")
    public void download(HttpServletResponse response, @PathVariable String token) throws Exception{
        //将文件上传
        //已经包含了COLD_TMP&
        File newFile = new File(OssColdDataConstant.POSITION+"\\"+token);
        FileInputStream fis = new FileInputStream(newFile);
        ControllerUtils.loadResource(response, fis, token,true,
                null);
    }

    @DeleteMapping("/delete/{token}")
    public Boolean del(@PathVariable String token) {
        //将文件解压缩
        File oldFile = new File(OssColdDataConstant.POSITION+"\\"+token);
        if(!oldFile.exists()){
            return true;
        }else{
            return oldFile.delete();
        }
    }

    @DeleteMapping("/deleteNor/{etag}")
    public Boolean deleteNor(@PathVariable String etag) {
        //将文件解压缩
        File oldFile = new File(OssColdDataConstant.POSITION+"\\"+FILE_COLD+etag);
        if(!oldFile.exists()){
            return true;
        }else{
            return oldFile.delete();
        }
    }
}
