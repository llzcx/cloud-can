package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理Bucket接口
 * @author 杨世博
 */
@RestController
@RequestMapping("/manageBucket")
public class ManageBucketController {

    @Autowired
    private IManageBucketService manageBucketService;

    /**
     * 获取bucket列表
     * 1-分页（无筛选）
     * 2-根据用户Id筛选
     * @param userId 代查找用户的Id
     * @param pageNum
     * @param size
     * @return
     */
    @GetMapping("/listBuckets")
    public ApiResp<RPage<Bucket>> listBuckets(@RequestParam("userId") Long userId,
                                              @RequestParam("pageNum")Integer pageNum,
                                              @RequestParam("size")Integer size){
        RPage<Bucket> bucketRPage = manageBucketService.getBucketList(userId,pageNum,size);
        return ApiResp.success(bucketRPage);
    }

    /**
     * 删除Bucket及其与之相关的所有信息所有
     * 1-收藏
     * @param userId Bucket拥有者的Id
     * @param name Bucket的名字
     * @return
     */
    @DeleteMapping("/deleteBucket")
    public ApiResp<Boolean> deleteBucket(@RequestParam("userId")Long userId,@RequestParam("name")String name) throws Exception{
        Boolean flag = manageBucketService.deleteBucket(userId, name);
        return ApiResp.ifResponse(flag,flag, ResultCode.COMMON_FAIL);
    }
}
