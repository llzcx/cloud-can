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
     * @param bucketName
     * @param objectName
     * @param key
     * @param value
     * @return
     */
    @PutMapping("/putObjectTag")
    public ApiResp<List<ObjectTag>> putObjectTag(@RequestParam("bucketName")String bucketName, @RequestParam("objectName") String objectName, @RequestParam("key") String key, @RequestParam("value") String value){
        objectTagService.putObjectTag(bucketName,objectName,key,value);
        return ApiResp.success();
    }

    /**
     * 删除对象标签
     * @param bucketName
     * @param objectName
     * @param tagId 标签的id
     * @return
     */
    @DeleteMapping("/deleteObjectTag")
    public ApiResp<Boolean> deleteObjectTag(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName, @RequestParam("TagId") Long tagId){
        Boolean aBoolean = objectTagService.deleteObjectTag(bucketName, objectName, tagId);
        return ApiResp.ifResponse(aBoolean,aBoolean, ResultCode.COMMON_FAIL);
    }
}
