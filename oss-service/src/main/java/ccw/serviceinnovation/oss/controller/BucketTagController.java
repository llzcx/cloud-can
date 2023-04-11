package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.BucketTag;
import ccw.serviceinnovation.common.entity.ObjectTag;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.dto.DeleteBucketTagDto;
import ccw.serviceinnovation.oss.pojo.dto.PutBucketTagDto;
import ccw.serviceinnovation.oss.service.IBucketTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static ccw.serviceinnovation.common.constant.AuthorityConstant.API_BUCKET;

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
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_LIST, name = "getBucketTag",description = "获取Bucket标签列表")
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
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_WRITER, name = "putBucketTag",description = "添加Bucket标签")
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
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_WRITER, name = "deleteBucketTag",description = "删除bucket标签")
    public ApiResp<List<BucketTag>> deleteBucketTag(@RequestBody DeleteBucketTagDto bucketTags){
        List<BucketTag> newBucketTags = bucketTagService.deleteBucketTag(bucketTags);
        return ApiResp.success(newBucketTags);
    }
}
