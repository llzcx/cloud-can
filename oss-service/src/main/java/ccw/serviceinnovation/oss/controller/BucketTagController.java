package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.entity.BucketTag;
import ccw.serviceinnovation.common.entity.ObjectTag;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.service.IBucketTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Joy Yang
 *
 * Bucket标签接口
 */
@RestController
@RequestMapping("/bucketTag")
public class BucketTagController {

    @Autowired
    private IBucketTagService bucketTagService;

    /**
     * 获取Bucket标签列表
     * @param bucketName
     * @return
     */
    @GetMapping("/getBucketTag")
    public ApiResp<List<BucketTag>> getBucketTag(@RequestParam("bucketName") String bucketName){
        List<BucketTag> bucketTags = bucketTagService.getBucketTag(bucketName);
        return ApiResp.success(bucketTags);
    }

    /**
     * 添加Bucket标签
     * 1-判断是否有更改标签权限
     * 2-判断key是否相同
     * 3-添加
     * 可同时添加多个标签
     * @param bucketName
     * @param bucketTags
     * @return
     */
    @PutMapping("/putBucketTag")
    public ApiResp<List<BucketTag>> putBucketTag(@RequestParam("bucketName")String bucketName, @RequestBody List<BucketTag> bucketTags){
        List<BucketTag> newBucketTags = bucketTagService.putBucketTag(bucketName,bucketTags);
        return ApiResp.success(newBucketTags);
    }

    /**
     * 删除bucket标签
     * @param bucketName
     * @param tagId 标签的id
     * @return
     */
    @DeleteMapping("/deleteBucketTag")
    public ApiResp<Boolean> deleteBucketTag(@RequestParam("bucketName") String bucketName, @RequestParam("TagId") Long tagId){
        Boolean aBoolean = bucketTagService.deleteBucketTag(bucketName, tagId);
        return ApiResp.ifResponse(aBoolean,aBoolean, ResultCode.COMMON_FAIL);
    }
}
