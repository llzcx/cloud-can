package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.Authorize;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.dto.PutAuthorizeDto;
import ccw.serviceinnovation.oss.service.IAuthorizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * bucket授权策略接口
 * @author 陈翔
 */
@RestController
@RequestMapping("/authorize")
public class AuthorizeController {

    @Autowired(required = false)
    HttpServletRequest request;

    @Autowired
    IAuthorizeService authorizeService;

    /**
     * 添加/更新一个bucket授权策略
     * @param putAuthorizeDto
     * @param bucketName
     * @param authorizeId
     * @return
     * @throws IOException
     */
    @PostMapping("/putAuthorize")
    @ResponseBody
    @OssApi(target = AuthorityConstant.API_BUCKET,type = AuthorityConstant.API_WRITER, name = "putAuthorize",description = "添加/更新一个bucket授权策略")
    public ApiResp<Boolean> putAuthorize(@RequestBody PutAuthorizeDto putAuthorizeDto,
                                @RequestParam(value = "bucketName")String bucketName,
                                @RequestParam(value = "authorizeId",required = false)Long authorizeId) throws IOException {
        return ApiResp.success(authorizeService.putAuthorize(putAuthorizeDto, bucketName, authorizeId));

    }

    /**
     * 获取权限策略列表
     * @param bucketName
     * @return
     * @throws IOException
     */
    @GetMapping("/listAuthorizes")
    @ResponseBody
    @OssApi(target = AuthorityConstant.API_BUCKET,type = AuthorityConstant.API_READ, name = "putAuthorize",description = "获取权限策略列表")
    public ApiResp<List<Authorize>> listAuthorizes(@RequestParam("bucketName")String bucketName) throws IOException {
        return ApiResp.success(authorizeService.listAuthorizes(bucketName));
    }


    /**
     * 删除一个授权策略
     * @param bucketName
     * @param authorizeId
     * @return
     * @throws IOException
     */
    @DeleteMapping("/deleteAuthorize")
    @ResponseBody
    @OssApi(target = AuthorityConstant.API_BUCKET,type = AuthorityConstant.API_WRITER, name = "putAuthorize",description = "删除一个授权策略")
    public ApiResp<Boolean> deleteAuthorize(@RequestParam("bucketName") String bucketName, @RequestParam("authorizeId")Long authorizeId) throws IOException {
        return ApiResp.success(authorizeService.deleteAuthorize(bucketName,authorizeId));
    }
}
