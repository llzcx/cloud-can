package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.oss.pojo.vo.AllBucketMessageVo;
import ccw.serviceinnovation.oss.service.IAllBucketMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 杨世博
 * 获取用户对象存储可展示数据的接口
 */
@RestController
@RequestMapping("/allBucketMessage")
public class AllBucketMessageController {

    @Autowired
    IAllBucketMessageService allBucketMessageService;

    @GetMapping("getMessage")
    public ApiResp<AllBucketMessageVo> getMessage(){
        return ApiResp.success(allBucketMessageService.getMessage());
    }
}
