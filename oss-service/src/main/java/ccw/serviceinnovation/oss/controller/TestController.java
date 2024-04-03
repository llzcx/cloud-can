package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    /**
     * 下载对象
     *
     * @param objectName 对象名
     * @param bucketName 桶名
     * @return HttpServletResponse流中返回二进制数据
     * @throws Exception
     */
    @GetMapping("/1")
    @OssApi(target = AuthorityConstant.API_BUCKET, type = AuthorityConstant.API_READ, name = "test", description = "test")
    public ApiResp<String> test(@RequestParam("bucketName") String bucketName,@RequestParam("name") String name) throws Exception {
        log.info(name);
        return ApiResp.success(name);
    }
}
