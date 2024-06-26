package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.vo.BucketVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
     * 1-分页
     * 2-根据用户名、用户ID筛选
     * @param keyword 用户名、用户ID
     * @param pageNum 当前页数
     * @param size 每页大小
     * @return 筛选后分页的bucket列表
     */
    @GetMapping("/listBuckets")
    @OssApi(target = AuthorityConstant.API_MANAGE,type = AuthorityConstant.API_LIST, name = "listsFragment",description = "获取一个桶内所有碎片")
    public ApiResp<RPage<BucketVo>> listBuckets(@RequestParam(value = "keyword",required = false) String keyword,
                                                @RequestParam("pageNum")Integer pageNum,
                                                @RequestParam("size")Integer size){
        RPage<BucketVo> bucketRPage = manageBucketService.getBucketList(keyword,pageNum,size);
        return ApiResp.success(bucketRPage);
    }

    /**
     * 删除Bucket及其与之相关的所有信息所有
     * @param userId Bucket拥有者的Id
     * @param name Bucket的名字
     * @return 删除结果
     */
    @DeleteMapping("/deleteBucket")
    @OssApi(target = AuthorityConstant.API_MANAGE,type = AuthorityConstant.API_WRITER, name = "deleteBucket",description = "删除Bucket及其与之相关的所有信息所有")
    public ApiResp<Boolean> deleteBucket(@RequestParam("userId")Long userId,@RequestParam("name")String name) throws Exception{
        Boolean flag = manageBucketService.deleteBucket(userId, name);
        return ApiResp.ifResponse(flag,flag, ResultCode.COMMON_FAIL);
    }
}
