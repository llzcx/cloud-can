package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.entity.ObjectTag;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
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
     * @return 该对象的标签
     */
    @GetMapping("/getObjectTag")
    public ApiResp<List<ObjectTag>> getObjectTag(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName){
        List<ObjectTag> objectTagList = objectTagService.getObjectTag(bucketName,objectName);
        return ApiResp.success(objectTagList);
    }

    /**
     * 添加对象标签
     * 可同时添加多个标签
     * @param objectTagDto 对象标签
     * @return 添加对象后的该对象的标签
     */
    @PutMapping("/putObjectTag")
    public ApiResp<List<ObjectTag>> putObjectTag(@RequestBody PutObjectTagDto objectTagDto){
        List<ObjectTag> newObjectTags = objectTagService.putObjectTag(objectTagDto.getBucketName(),objectTagDto.getObjectName(),objectTagDto.getObjectTags());
        return ApiResp.success(newObjectTags);
    }

    /**
     * 删除对象标签
     * @param objectTagDto 对象标签
     * @return 删除后该对象的对象标签
     */
    @DeleteMapping("/deleteObjectTag")
    public ApiResp<List<ObjectTag>> deleteObjectTag(@RequestBody DeleteObjectTagDto objectTagDto){
        List<ObjectTag> newObjectTags = objectTagService.deleteObjectTag(objectTagDto.getBucketName(),objectTagDto.getObjectName(),objectTagDto.getObjectTags());
        return ApiResp.success(newObjectTags);
    }
}
