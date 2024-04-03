package ccw.serviceinnvation.example.bootdemo.cloudcan;

import ccw.serviceinnvation.sdk.CloudCan;
import ccw.serviceinnvation.sdk.exception.CloudCanDownLoadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/test")
public class CloudCanController {

    @Autowired(required = false)
    CloudCan cloudCan;
    @PostMapping("/create/{bucketName}")
    public String createBucket(@PathVariable String bucketName) throws IOException {
        cloudCan.createBucket(bucketName);
        return Constant.RES_OK;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return "Please select a file to upload";
        }
        try {
            cloudCan.putObject(Constant.BUCKET,file.getName(),file.getInputStream());
            return Constant.RES_OK;
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload file";
        }
    }

    @GetMapping("/download/{objName}")
    public void download(@PathVariable String objName, HttpServletResponse response) throws IOException, CloudCanDownLoadException {
        cloudCan.getObject(Constant.BUCKET,objName,response.getOutputStream());
    }
}
