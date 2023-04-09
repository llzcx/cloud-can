package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.BucketTag;
import ccw.serviceinnovation.common.entity.ObjectTag;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.pojo.dto.DeleteBucketTagDto;
import ccw.serviceinnovation.oss.pojo.dto.PutBucketTagDto;
import ccw.serviceinnovation.oss.service.IBucketTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Bucket标签接口
 * @author Joy Yang
 */
@RestController
@RequestMapping("/bucketTag")
public class BucketTagController {

    @Autowired
    private IBucketTagService bucketTagService;

    /**
     * 获取Bucket标签列表
     * @param bucketName 桶名
     * @return
     */
    @GetMapping("/getBucketTag")
    public ApiResp<List<BucketTag>> getBucketTag(@RequestParam("bucketName") String bucketName){
        List<BucketTag> bucketTags = bucketTagService.getBucketTag(bucketName);
        return ApiResp.success(bucketTags);
    }

    /**
     * 添加Bucket标签
     * 可同时添加多个标签
     * @param bucketTags 添加的标签的信息
     * @return
     */
    @PutMapping("/putBucketTag")
    public ApiResp<List<BucketTag>> putBucketTag(@RequestBody PutBucketTagDto bucketTags){
        List<BucketTag> newBucketTags = bucketTagService.putBucketTag(bucketTags);
        return ApiResp.success(newBucketTags);
    }

    /**
     * 删除bucket标签
     * @param bucketTags 标签的id
     * @return
     */
    @DeleteMapping("/deleteBucketTag")
    public ApiResp<List<BucketTag>> deleteBucketTag(@RequestBody DeleteBucketTagDto bucketTags){
        List<BucketTag> newBucketTags = bucketTagService.deleteBucketTag(bucketTags);
        return ApiResp.success(newBucketTags);
    }
}
