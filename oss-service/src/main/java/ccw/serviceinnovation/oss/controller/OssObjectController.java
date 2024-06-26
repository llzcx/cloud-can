package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.bo.BlockTokenBo;
import ccw.serviceinnovation.oss.pojo.dto.BatchDeletionObjectDto;
import ccw.serviceinnovation.oss.pojo.vo.*;
import ccw.serviceinnovation.oss.service.IObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import static ccw.serviceinnovation.common.constant.AuthorityConstant.API_BUCKET;
import static ccw.serviceinnovation.common.constant.AuthorityConstant.API_OBJECT;

/**
 * 对象接口
 *
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
     * 下载对象
     *
     * @param objectName 对象名
     * @param bucketName 桶名
     * @return HttpServletResponse流中返回二进制数据
     * @throws Exception
     */
    @GetMapping("/download")
    @OssApi(target = AuthorityConstant.API_OBJECT, type = AuthorityConstant.API_READ, name = "download", description = "下载对象")
    public void download(@RequestParam("objectName") String objectName, @RequestParam("bucketName") String bucketName, HttpServletResponse response) throws Exception {
        objectService.download(bucketName, objectName, response);
    }

    /**
     * 对象的元数据
     *
     * @param objectName 对象名
     * @param bucketName 桶名
     * @return 对象元数据信息
     * @throws Exception
     */
    @GetMapping("/getObjectInfo")
    @OssApi(target = AuthorityConstant.API_OBJECT, type = AuthorityConstant.API_READ, name = "getObjectInfo", description = "从桶中获取一个对象的元数据")
    public ApiResp<OssObjectVo> getObjectInfo(@RequestParam("objectName") String objectName, @RequestParam("bucketName") String bucketName) throws Exception {
        OssObjectVo object = objectService.getObjectInfo(bucketName, objectName);
        return ApiResp.ifResponse(object != null, object, ResultCode.COMMON_FAIL);
    }

    /**
     * 在桶中添加一个文件夹
     *
     * @param bucketName     桶名称
     * @param objectName     文件夹名称
     * @param parentObjectId 父级对象ID
     * @return 返回是否添加成功
     * @throws Exception
     */
    @PutMapping("/putFolder")
    @OssApi(target = AuthorityConstant.API_BUCKET, type = AuthorityConstant.API_WRITER, name = "putFolder", description = "在桶中添加一个文件夹")
    public ApiResp<Boolean> putFolder(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName,
                                      @RequestParam(value = "parentObjectId", required = false) Long parentObjectId) throws Exception {
        return ApiResp.success(objectService.putFolder(bucketName, objectName, parentObjectId));
    }

    /**
     * 在桶中添加一个对象[小文件]
     *
     * @param bucketName     桶名
     * @param objectName     对象名[可以包含/,将自动创建文件夹]
     * @param etag           文件hash
     * @param parentObjectId 声明处于哪个文件夹下
     * @param objectAcl      对象权限控制
     * @param file           文件数据
     * @return 返回是否上传成功
     * @throws Exception
     */
    @PutMapping("/upload")
    @OssApi(target = AuthorityConstant.API_BUCKET, type = AuthorityConstant.API_WRITER, name = "putSmallObject", description = "在桶中添加一个对象[小文件]")
    public ApiResp<Boolean> putSmallObject(@RequestParam("bucketName") String bucketName, String objectName,
                                           String etag, @RequestParam(value = "parentObjectId", required = false) Long parentObjectId,
                                           Integer objectAcl,
                                           MultipartFile file) throws Exception {
        return ApiResp.success(objectService.upload(bucketName, objectName, etag, file, parentObjectId, objectAcl));
    }


    /**
     * 创建一个文件分块上传事件
     *
     * @param bucketName     桶名字
     * @param objectName     对象名字
     * @param etag           hash值
     * @param size           大小(单位:B)
     * @param chunks         总块数
     * @param parentObjectId 父级对象id(文件夹对象)
     * @return 该事件的唯一ID / 或者直接上传成功
     * @throws Exception
     */
    @PostMapping("/createUploadEvent")
    @OssApi(target = AuthorityConstant.API_BUCKET, type = AuthorityConstant.API_WRITER, name = "createChunkToken", description = "创建一个文件分块上传事件")
    public ApiResp<BlockTokenBo> createChunkToken(@RequestParam("bucketName") String bucketName,
                                                  String objectName,
                                                  String etag,
                                                  Long size,
                                                  Integer chunks,
                                                  Long parentObjectId,
                                                  Integer objectAcl) throws Exception {
        return ApiResp.success(objectService.createUploadEvent(etag, bucketName, objectName, parentObjectId, objectAcl, chunks, size));
    }

    /**
     * 追加分片
     *
     * @param bucketName 桶名
     * @param eventId    事件id
     * @param chunk      第几块分片
     * @param file       数据流
     * @return
     * @throws Exception
     */
    @PostMapping("/append")
    @OssApi(target = AuthorityConstant.API_BUCKET, type = AuthorityConstant.API_WRITER, name = "createChunkToken", description = "创建一个文件分块上传事件")
    public ApiResp<Boolean> append(@RequestParam("bucketName") String bucketName,
                                   String eventId,
                                   Integer chunk,
                                   MultipartFile file) throws Exception {

        return ApiResp.success(objectService.append(file, chunk, eventId, bucketName));
    }


    /**
     * 合并文件分块
     *
     * @param eventId    文件事件的id
     * @param bucketName 桶名字
     * @return 返回是否合并成功
     * @throws Exception
     */
    @PostMapping("/merge")
    @OssApi(target = AuthorityConstant.API_BUCKET, type = AuthorityConstant.API_WRITER, name = "putBigObject", description = "在桶中添加一个对象[大文件]")
    public ApiResp<Boolean> merge(String bucketName, String eventId) throws Exception {
        return ApiResp.success(objectService.merge(bucketName, eventId));
    }


    /**
     * 从桶中删除一个对象
     *
     * @param bucketName 桶名
     * @param objectName 对象名
     * @return 返回添加的桶对象
     * @throws Exception
     */
    @DeleteMapping("/deleteObject")
    @OssApi(target = AuthorityConstant.API_OBJECT, type = AuthorityConstant.API_WRITER, name = "deleteObject", description = "从桶中删除一个对象")
    public ApiResp<Boolean> deleteObject(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName) throws Exception {
        return ApiResp.ifResponse(objectService.deleteObject(bucketName, objectName), null, ResultCode.COMMON_FAIL
        );
    }

    /**
     * 删除桶内全部对象
     *
     * @param bucketName 桶名
     * @return 返回添加的桶对象
     * @throws Exception
     */
    @DeleteMapping("/deleteAll")
    @OssApi(target = API_BUCKET, type = AuthorityConstant.API_WRITER, name = "deleteAll", description = "删除所有对象")
    public ApiResp<Boolean> deleteAll(@RequestParam("bucketName") String bucketName) throws Exception {
        return ApiResp.ifResponse(objectService.deleteAll(bucketName), null, ResultCode.COMMON_FAIL);
    }

    /**
     * 获取对象列表
     *
     * @param bucketName     桶名
     * @param key            桶名的关键字
     * @param pageNum        第几页
     * @param size           每页大小
     * @param parentObjectId 父级文件夹id
     * @param isImages       是否筛选出图片
     * @return 对象列表数据
     * @throws Exception
     */
    @GetMapping("/listObjects")
    @OssApi(target = AuthorityConstant.API_BUCKET, type = AuthorityConstant.API_LIST, name = "listObjects", description = "获取对象列表")
    public ApiResp<RPage<ObjectVo>> listObjects(@RequestParam("bucketName") String bucketName, @RequestParam(value = "key", required = false) String key,
                                                @RequestParam("pagenum") Integer pageNum, @RequestParam("size") Integer size,
                                                @RequestParam(value = "parentObjectId", required = false) Long parentObjectId, @RequestParam(value = "isImages", required = false) Boolean isImages) throws Exception {
        RPage<ObjectVo> rPage = objectService.listObjects(bucketName, pageNum, size, key, parentObjectId, isImages);
        return ApiResp.success(rPage);
    }


    /**
     * 归档一个文件
     *
     * @param bucketName 桶名
     * @param objectName 对象名
     * @return 是否归档成功
     * @throws Exception
     */
    @PostMapping("/freeze")
    @OssApi(target = AuthorityConstant.API_OBJECT, type = AuthorityConstant.API_WRITER, name = "freeze", description = "归档一个文件")
    public ApiResp<Boolean> freeze(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName) throws Exception {
        return ApiResp.success(objectService.freeze(bucketName, objectName));
    }


    /**
     * 解冻一个文件
     *
     * @param bucketName 桶名
     * @param objectName 待解冻的对象名
     * @return 是否解冻成功
     * @throws Exception
     */
    @PostMapping("/unfreeze")
    @OssApi(target = AuthorityConstant.API_OBJECT, type = AuthorityConstant.API_WRITER, name = "unfreeze", description = "解冻一个文件")
    public ApiResp<Boolean> unfreeze(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName) throws Exception {
        return ApiResp.success(objectService.unfreeze(bucketName, objectName));
    }

    /**
     * 备份一个对象
     *
     * @param bucketName       源对象所处的桶名
     * @param objectName       源对象的对象名
     * @param targetBucketName 目标桶
     * @param newObjectName    在目标桶中的新名字
     * @return 是否备份成功
     * @throws Exception
     */
    @PostMapping("/backup")
    @OssApi(target = AuthorityConstant.API_OBJECT, type = AuthorityConstant.API_BACK_UP, name = "backup", description = "备份一个对象")
    public ApiResp<Boolean> backup(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName,
                                   @RequestParam("targetBucketName") String targetBucketName,
                                   @RequestParam(value = "newObjectName", required = false) String newObjectName) throws Exception {
        return ApiResp.success(objectService.backup(bucketName, targetBucketName, objectName, newObjectName));
    }


    /**
     * 复原一个对象
     *
     * @param bucketName 备份对象-桶名
     * @param objectName 备份对象-对象名
     * @return 是否复原成功
     * @throws Exception
     */
    @PostMapping("/backupRecovery")
    @OssApi(target = AuthorityConstant.API_OBJECT, type = AuthorityConstant.API_BACKUP_RECOVERY, name = "backupRecovery", description = "复原一个对象")
    public ApiResp<Boolean> backupRecovery(@RequestParam("bucketName") String bucketName, @RequestParam("objectName") String objectName) throws Exception {
        return ApiResp.success(objectService.backupRecovery(bucketName, objectName));
    }

    /**
     * 重命名对象
     *
     * @param bucketName 桶名
     * @param objectName 对象名
     * @param newtName   新名字
     * @return 是否重命名成功
     * @throws Exception
     */
    @PutMapping("/updateObjectName")
    @OssApi(target = API_OBJECT, type = AuthorityConstant.API_WRITER, name = "updateObjectName", description = "重命名对象")
    public ApiResp<Boolean> updateObjectName(@RequestParam(value = "bucketName") String bucketName
            , @RequestParam(value = "objectName") String objectName, @RequestParam(value = "newtName") String newtName) throws Exception {
        Boolean flag = objectService.updateObjectName(bucketName, objectName, newtName);
        return ApiResp.ifResponse(flag, flag, ResultCode.COMMON_FAIL);
    }

    /**
     * 更新objectAcl
     *
     * @param bucketName 桶名
     * @param objectName 对象名
     * @param objectAcl  objectAcl编码
     * @return 是否更新成功
     * @throws Exception
     */
    @PutMapping("/updateObjectAcl")
    @OssApi(target = API_OBJECT, type = AuthorityConstant.API_WRITER, name = "updateObjectAcl", description = "更新objectAcl")
    public ApiResp<Boolean> updateObjectAcl(@RequestParam(value = "bucketName") String bucketName
            , @RequestParam(value = "objectName") String objectName, @RequestParam(value = "objectAcl") Integer objectAcl) throws Exception {
        Boolean flag = objectService.updateObjectAcl(bucketName, objectName, objectAcl);
        return ApiResp.ifResponse(flag, flag, ResultCode.COMMON_FAIL);
    }

    public static String getRequestBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = request.getReader();
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    /**
     * 批量删除
     *
     * @param bucketName             桶名
     * @param batchDeletionObjectDto 对象名字列表
     * @return
     * @throws Exception
     */
    @DeleteMapping("/batchDeletion")
    @OssApi(target = API_BUCKET, type = AuthorityConstant.API_WRITER, name = "batchDeletion", description = "批量删除")
    public ApiResp<Boolean> batchDeletion(@RequestParam(value = "bucketName") String bucketName
            , @RequestBody BatchDeletionObjectDto batchDeletionObjectDto, HttpServletRequest request) throws Exception {
        Boolean flag = objectService.batchDeletion(bucketName, batchDeletionObjectDto);
        return ApiResp.ifResponse(flag, flag, ResultCode.COMMON_FAIL);
    }


    /**
     * 获取一个对象的所有备份数据
     *
     * @param bucketName 桶名
     * @param objectName 对象名
     * @return
     * @throws Exception
     */
    @GetMapping("/listBackupObjects")
    @OssApi(target = API_OBJECT, type = AuthorityConstant.API_READ, name = "listBackupObjects", description = "批量删除")
    public ApiResp<List<BackupObjectVo>> listBackupObjects(@RequestParam(value = "bucketName") String bucketName
            , @RequestParam(value = "objectName") String objectName) throws Exception {
        return ApiResp.success(objectService.listBackupObjects(bucketName, objectName));
    }
}

