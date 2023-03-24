package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件下载和预览接口
 * @author 陈翔
 */

@RestController
@RequestMapping("/object")
public class ObjectController {
    /**
     * 下载文件
     * param objectId 对象ID
     * @return 返回添加的桶对象
     */
    @PostMapping("/download/{bucketName}/{objectName}")
    @OssApi(target = AuthorityConstant.API_OBJECT,type = AuthorityConstant.API_READ,name = "getObjectInfo",description = "从桶中获取一个对象的真实数据")
    public ApiResp<OssObject> getObject(@PathVariable String bucketName, @PathVariable String objectName) throws Exception{
        throw new OssException(ResultCode.REQUEST_ADDRESS_ERROR);
    }
    /**
     * 预览图片接口
     * param objectId 对象ID
     * @return 返回添加的桶对象
     */
    @PostMapping("/preview-image/{bucketName}/{objectName}")
    @OssApi(target = AuthorityConstant.API_OBJECT,type = AuthorityConstant.API_READ,name = "getObjectInfo",description = "从桶中获取一个对象的真实数据")
    public ApiResp<OssObject> previewImage(@PathVariable String bucketName, @PathVariable String objectName) throws Exception{
        throw new OssException(ResultCode.REQUEST_ADDRESS_ERROR);
    }
    /**
     * 下载文件
     * param objectId 对象ID
     * @return 返回添加的桶对象
     */
    @PostMapping("/preview-video/{bucketName}/{objectName}")
    @OssApi(target = AuthorityConstant.API_OBJECT,type = AuthorityConstant.API_READ,name = "getObjectInfo",description = "从桶中获取一个对象的真实数据")
    public ApiResp<OssObject> previewVideo(@PathVariable String bucketName, @PathVariable String objectName) throws Exception{
        throw new OssException(ResultCode.REQUEST_ADDRESS_ERROR);
    }
}
