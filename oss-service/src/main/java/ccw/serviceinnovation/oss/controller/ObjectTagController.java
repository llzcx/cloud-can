package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.ObjectTag;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.dto.DeleteObjectTagDto;
import ccw.serviceinnovation.oss.pojo.dto.PutObjectTagDto;
import ccw.serviceinnovation.oss.service.IObjectTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 对象标签接口
 * @author 杨世博
 */
@RestController
@RequestMapping("/objectTag")
public class ObjectTagController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    private IObjectTagService objectTagService;


    /**
     * 获取对象标签
     * @param objectName 对象名
     * @return
     */
    @GetMapping("/getObjectTag")
    @OssApi(target = AuthorityConstant.API_OBJECT,type = AuthorityConstant.API_READ, name = "getObjectTag",description = "获取对象标签")
    public ApiResp<List<ObjectTag>> getObjectTag(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName){
        List<ObjectTag> objectTagList = objectTagService.getObjectTag(bucketName,objectName);
        return ApiResp.success(objectTagList);
    }

    /**
     * 添加对象标签
     * 可同时添加多个标签
     * @param objectTagDto 对象标签
     * @return
     */
    @PutMapping("/putObjectTag")
    @OssApi(target = AuthorityConstant.API_OBJECT,type = AuthorityConstant.API_WRITER, name = "putObjectTag",description = "添加对象标签")
    public ApiResp<List<ObjectTag>> putObjectTag(@RequestBody PutObjectTagDto objectTagDto){
        List<ObjectTag> newObjectTags = objectTagService.putObjectTag(objectTagDto.getBucketName(),objectTagDto.getObjectName(),objectTagDto.getObjectTags());
        return ApiResp.success(newObjectTags);
    }

    /**
     * 删除对象标签
     * @param objectTagDto 对象标签
     * @return
     */
    @DeleteMapping("/deleteObjectTag")
    @OssApi(target = AuthorityConstant.API_OBJECT,type = AuthorityConstant.API_WRITER, name = "deleteObjectTag",description = "删除对象标签")
    public ApiResp<List<ObjectTag>> deleteObjectTag(@RequestBody DeleteObjectTagDto objectTagDto){
        List<ObjectTag> newObjectTags = objectTagService.deleteObjectTag(objectTagDto.getBucketName(),objectTagDto.getObjectName(),objectTagDto.getObjectTags());
        return ApiResp.success(newObjectTags);
    }
}
