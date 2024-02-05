package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.vo.AllBucketMessageVo;
import ccw.serviceinnovation.oss.service.IAllBucketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static ccw.serviceinnovation.common.constant.AuthorityConstant.API_BUCKET;

/**
 * 获取用户对象存储可展示数据的接口
 * @author 杨世博
 *
 */
@RestController
@RequestMapping("/allBucketMessage")
public class AllBucketMessageController {

    @Autowired
    IAllBucketMessageService allBucketMessageService;

    /**
     * 获取用户所有bucket中的展示数据
     * @return bucket中的展示数据
     */
    @GetMapping("/getMessage")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_READ, name = "getMessage",description = "获取用户所有bucket中的展示数据")
    public ApiResp<AllBucketMessageVo> getMessage(){
        return ApiResp.success(allBucketMessageService.getMessage());
    }
}
