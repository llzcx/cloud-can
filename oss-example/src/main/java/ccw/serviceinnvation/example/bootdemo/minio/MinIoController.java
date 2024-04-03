package ccw.serviceinnvation.example.bootdemo.minio;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import static ccw.serviceinnvation.example.bootdemo.cloudcan.Constant.RES_OK;

@RestController
@RequestMapping("/minio")
public class MinIoController {

    MinioUtil minioUtil = new MinioUtil();

    @Autowired
    StorageProperty storageProperty;

    /**
     * 上传文件
     *
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestBody MultipartFile file,String objectName) {
        MinioClient minioClient = MinioClientConfig.getMinioClient();
        if (minioClient == null) {
            return "连接MinIO服务器失败";
        }
        minioUtil.minioUpload(file, objectName, storageProperty.getBucketName());
        return RES_OK;
    }

    /**
     * 获取文件预览地址
     *
     * @param fileName
     * @return
     */
    @RequestMapping("/getRedFile")
    public String getRedFile(@RequestBody String fileName) {
        MinioClient minioClient = MinioClientConfig.getMinioClient();
        if (minioClient == null) {
            return "连接MinIO服务器失败";
        }

        return minioUtil.getPreviewFileUrl(storageProperty.getBucketName(), fileName);
    }

    /**
     * 下载文件
     *
     * @param fileName
     * @param response
     * @return
     */
    @RequestMapping("/downloadFile")
    public String downloadFile(@RequestParam String fileName, HttpServletResponse response) {
        MinioClient minioClient = MinioClientConfig.getMinioClient();
        if (minioClient == null) {
            return "连接MinIO服务器失败";
        }
        return minioUtil.downloadFile(storageProperty.getBucketName(), fileName, response) != null ? "下载成功" : "下载失败";
    }

    /**
     * 删除文件
     * @return
     */
    @PostMapping("/deleteFile")
    public String deleteFile() {
        MinioClient minioClient = MinioClientConfig.getMinioClient();
        if (minioClient == null) {
            return "连接MinIO服务器失败";
        }
        boolean flag = minioUtil.deleteBucketFile(storageProperty.getBucketName());
        return flag ? "删除成功" : "删除失败";
    }


}