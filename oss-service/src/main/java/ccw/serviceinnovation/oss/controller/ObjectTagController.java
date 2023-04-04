package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.entity.ObjectTag;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
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
     * @param objectName
     * @return
     */
    @GetMapping("/getObjectTag")
    public ApiResp<List<ObjectTag>> getObjectTag(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName){
        List<ObjectTag> objectTagList = objectTagService.getObjectTag(bucketName,objectName);
        return ApiResp.success(objectTagList);
    }

    /**
     * 添加对象标签
     * 1-判断是否有更改标签权限
     * 2-判断key是否相同
     * 3-添加
     * 可同时添加多个标签
     * @param bucketName
     * @param objectName
     * @param objectTags
     * @return
     */
    @PutMapping("/putObjectTag")
    public ApiResp<List<ObjectTag>> putObjectTag(@RequestParam("bucketName")String bucketName, @RequestParam("objectName") String objectName, @RequestBody List<ObjectTag> objectTags){
        List<ObjectTag> newObjectTags = objectTagService.putObjectTag(bucketName, objectName, objectTags);
        return ApiResp.success(newObjectTags);
    }

    /**
     * 删除对象标签
     * @param bucketName
     * @param objectName
     * @param objectTags 标签的id
     * @return
     */
    @DeleteMapping("/deleteObjectTag")
    public ApiResp<List<ObjectTag>> deleteObjectTag(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName, @RequestBody List<ObjectTag> objectTags){
        List<ObjectTag> newObjectTags = objectTagService.deleteObjectTag(bucketName, objectName, objectTags);
        return ApiResp.success(newObjectTags);
    }
}
