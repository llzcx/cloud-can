package ccw.serviceinnovation.osscolddata.controller;
import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.nacos.TrackerService;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.file.ZipUtil;
import ccw.serviceinnovation.common.util.http.FileUtil;
import ccw.serviceinnovation.common.util.http.HttpUtils;
import ccw.serviceinnovation.osscolddata.constant.OssColdDataConstant;
import ccw.serviceinnovation.osscolddata.util.ControllerUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import service.raft.client.RaftRpcRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.ConcurrentHashMap;
import static ccw.serviceinnovation.osscolddata.constant.FilePrefixConstant.FILE_COLD;
import static ccw.serviceinnovation.osscolddata.constant.FilePrefixConstant.FILE_COLD_TMP;

/**
 * @author 陈翔
 */

@RestController("/cold")
public class OssColdController {

    public static ConcurrentHashMap<String,String> data = new ConcurrentHashMap<>();


    /**
     * oss-data调用此接口 将oss-cold-data 数据解冻到 oss-data
     * @param etag
     * @return
     */
    @PostMapping("/freeze/{ip}/{port}/{etag}")
    public void freeze(@PathVariable String etag, HttpServletResponse response, @PathVariable String ip, @PathVariable String port) throws Exception{
        //将文件下载
        FileUtil.saveFile("http://"+ip+":"+port+"/group/"+etag,OssColdDataConstant.POSITION+"\\"+ FILE_COLD_TMP +etag);
        File oldFile = new File(OssColdDataConstant.POSITION+"\\"+ FILE_COLD_TMP +etag);
        File newFile = new File(OssColdDataConstant.POSITION+"\\"+ FILE_COLD +etag);
        ZipUtil.decompressZip2Files(oldFile,newFile);
        oldFile.delete();
    }

    /**
     * oss-data调用此接口 将oss-cold-data 数据解冻到 oss-data
     * @param etag
     * @return
     */
    @PostMapping("/unfreeze/{etag}/{name}")
    public void unfreeze(@PathVariable String etag, HttpServletResponse response, @PathVariable String name) throws Exception{
        //将文件解压缩
        File oldFile = new File(OssColdDataConstant.POSITION+"\\"+ FILE_COLD +etag);
        File newFile = new File(OssColdDataConstant.POSITION+"\\"+ FILE_COLD_TMP +etag);
        File[] files = new File[1];
        files[0] = oldFile;
        ZipUtil.archiveFiles2Zip(files,newFile,false);
        //将文件上传
        FileInputStream fis = new FileInputStream(OssColdDataConstant.POSITION +"\\"+ FILE_COLD +etag);
        ControllerUtils.loadResource(response, fis,name,true,
                null);
        newFile.delete();
    }

    @DeleteMapping("/delete/{etag}")
    public void unfreeze(@PathVariable String etag, HttpServletResponse response) throws Exception{
        //将文件解压缩
        File oldFile = new File(OssColdDataConstant.POSITION+"\\"+ FILE_COLD +etag);
        oldFile.delete();
    }
}
