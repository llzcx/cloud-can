package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.ControllerUtils;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.bo.GetObjectBo;
import ccw.serviceinnovation.oss.pojo.vo.ObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 对象接口
 * @author 陈翔
 * @since 2023-01-20
 */
@RestController
@RequestMapping("/ossObject")
public class OssObjectController {

    @Autowired(required = false)
    HttpServletRequest request;

    @Autowired
    IObjectService objectService;

    /**
     * 从桶中获取一个对象的元数据
     * param objectId 对象ID
     * @return 返回添加的桶对象
     */
    @GetMapping("/getObjectInfo")
    @OssApi(target = AuthorityConstant.API_OBJECT,type = AuthorityConstant.API_READ,name = "getObjectInfo",description = "从桶中获取一个对象的元数据")
    public ApiResp<OssObject> getObjectInfo(@RequestParam("objectName") String objectName, @RequestParam("bucketName") String bucketName) throws Exception{
        OssObject object = objectService.getObjectInfo(bucketName,objectName);
        return ApiResp.ifResponse(object!=null,object,ResultCode.COMMON_FAIL);
    }

    /**
     * 下载文件[此接口的ip为网关层,在service服务没有实现]
     * param objectId 对象ID
     * @return 返回添加的桶对象
     */
    @PostMapping("/object/download/{bucketName}/{objectName}")
    @OssApi(target = AuthorityConstant.API_OBJECT,type = AuthorityConstant.API_READ,name = "getObjectInfo",description = "从桶中获取一个对象的真实数据")
    public ApiResp<OssObject> getObject(@PathVariable String bucketName, @PathVariable String objectName,Boolean download) throws Exception{
       throw new OssException(ResultCode.REQUEST_ADDRESS_ERROR);
    }

    /**
     * 在桶中添加一个文件夹
     * @param bucketName 桶名称
     * @param objectName 文件夹名称
     * @param parentObjectId 父级对象ID
     * @return
     * @throws Exception
     */
    @PutMapping("/putFolder")
    @OssApi(target = AuthorityConstant.API_BUCKET,type = AuthorityConstant.API_WRITER,name = "putFolder",description = "在桶中添加一个文件夹")
    public ApiResp<Boolean> putFolder(@RequestParam("bucketName") String bucketName,@RequestParam("objectName") String objectName,
    @RequestParam(value = "parentObjectId",required = false) Long parentObjectId) throws Exception{
        return ApiResp.success(objectService.putFolder(bucketName,objectName,parentObjectId));
    }

    /**
     * 在桶中添加一个对象[小文件]
     * @param objectName
     * @param md5
     * @param file
     * @return
     * @throws Exception
     */
    @PutMapping("/putSmallObject")
    @OssApi(target = AuthorityConstant.API_BUCKET,type = AuthorityConstant.API_WRITER,name = "putSmallObject",description = "在桶中添加一个对象[小文件]")
    public ApiResp<Boolean> putSmallObject(@RequestParam("bucketName") String bucketName, String objectName,
                                           String md5,@RequestParam(value = "parentObjectId",required = false) Long parentObjectId,
                                           MultipartFile file) throws Exception{
        return ApiResp.success(objectService.addSmallObject(bucketName,objectName,md5,file,parentObjectId));
    }


    /**
     * 创建一个文件分块上传事件
     * @param bucketName
     * @param objectName
     * @param etag
     * @param size
     * @param chunks
     * @param parentObjectId
     * @return 该事件的唯一ID
     * @throws Exception
     */
    @PostMapping("/createChunkToken")
    @OssApi(target = AuthorityConstant.API_BUCKET,type = AuthorityConstant.API_WRITER,name = "putBigObject",description = "在桶中添加一个对象[大文件]")
    public ApiResp<String> putBigObject(@RequestParam("bucketName") String bucketName,
                                         String objectName,
                                         String etag,
                                         Long size,
                                         Integer chunks,
                                         Long parentObjectId) throws Exception{
        return ApiResp.success(objectService.getBlockToken(etag, bucketName, objectName, parentObjectId, chunks, size));
    }


    /**
     * 合并文件分块
     * @param blockToken
     * @return
     * @throws Exception
     */
    @PostMapping("/merge")
    @OssApi(target = AuthorityConstant.API_BUCKET,type = AuthorityConstant.API_WRITER,name = "putBigObject",description = "在桶中添加一个对象[大文件]")
    public ApiResp<Boolean> merge(String blockToken) throws Exception{
        return ApiResp.success(objectService.mergeObjectChunk(blockToken));
    }


    /**
     * 从桶中删除一个对象
     * @param bucketName
     * @param objectName
     * @return 返回添加的桶对象
     * @throws Exception
     */
    @DeleteMapping("/deleteObject")
    @OssApi(target = AuthorityConstant.API_OBJECT,type = AuthorityConstant.API_WRITER,name = "deleteObject",description = "从桶中删除一个对象")
    public ApiResp<Boolean> deleteObject(@RequestParam("bucketName") String bucketName, @RequestParam("ObjectName") String objectName) throws Exception{
        return ApiResp.ifResponse(
                objectService.deleteObject(bucketName,objectName),
                null,
                ResultCode.COMMON_FAIL
        );
    }

    /**
     * 获取对象列表
     * @param bucketName
     * @return
     * @throws Exception
     */
    @GetMapping("/listObjects")
    @OssApi(target = AuthorityConstant.API_BUCKET,type = AuthorityConstant.API_LIST,name = "listObjects",description = "获取对象列表")
    public ApiResp<RPage<ObjectVo>> listObjects(@RequestParam("bucketName") String bucketName,@RequestParam("key") String key,
                                                @RequestParam("pagenum") Integer pageNum, @RequestParam("size") Integer size,
                                                @RequestParam("parentObjectId") Long parentObjectId,@RequestParam("isImages") Boolean isImages) throws Exception{
        RPage<ObjectVo> rPage = objectService.listObjects(bucketName, pageNum, size, key,parentObjectId,isImages);
        return ApiResp.success(rPage);
    }


}

