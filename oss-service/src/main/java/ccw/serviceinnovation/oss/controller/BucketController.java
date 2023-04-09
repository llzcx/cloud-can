package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.dto.AddBucketDto;
import ccw.serviceinnovation.oss.pojo.dto.BatchDeletionObjectDto;
import ccw.serviceinnovation.oss.pojo.vo.BucketFileTypeVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IBucketService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ccw.serviceinnovation.common.constant.AuthorityConstant.API_BUCKET;


/**
 *bucket接口
 * @author 陈翔
 * @since 2023-01-20
 */
@RestController
@RequestMapping("/bucket")
@Slf4j
public class BucketController {

    @Autowired(required = false)
    HttpServletRequest request;

    @Autowired
    IBucketService bucketService;


    /**
     * 获取桶信息
     * @param bucketName 桶名字
     * @return 返回结果集
     */
    @GetMapping("/getBucketInfo")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_READ, name = "getBucketInfo",description = "获取桶信息")
    @ApiOperation(value = "获取桶信息", notes = "操作对象:"+API_BUCKET)
    public ApiResp<Bucket> getBucketInfo(@RequestParam(value = "bucketName") String bucketName) throws Exception{
        Bucket bucketList = bucketService.getBucketInfo(bucketName);
        return ApiResp.success(bucketList);
    }


    /**
     * 获取桶列表
     * @param pageNum 当前页数
     * @param size 大小
     * @param key 桶名字关键词
     * @return
     * @throws Exception
     */
    @GetMapping("/listBuckets")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_READ, name = "listBuckets",description = "获取桶列表")
    public ApiResp<RPage<Bucket>> listBuckets(Integer pageNum, Integer size, String key) throws Exception{
        log.info("{},{},{}",pageNum,size,key);
        return ApiResp.success(bucketService.getBucketList(JwtUtil.getID(request),pageNum,size,key));
    }

    /**
     * 创建一个桶
     * @param addBucketDto dto
     * @return
     * @throws Exception
     */
    @PostMapping("/createBucket")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_WRITER, name = "createBucket",description = "创建一个桶")
    public ApiResp<Bucket> createBucket(@RequestBody AddBucketDto addBucketDto) throws Exception{
        return ApiResp.success(bucketService.createBucket(addBucketDto,JwtUtil.getID(request)));
    }


    /**
     * 删除一个桶
     * @param bucketName 桶名
     * @return
     * @throws Exception
     */
    @DeleteMapping("/deleteBucket")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_WRITER, name = "deleteBucket",description = "删除一个桶")
    public ApiResp<Boolean> deleteBucket(@RequestParam(value = "bucketName") String bucketName) throws Exception {
        Boolean flag = bucketService.deleteBucket(bucketName);
        return ApiResp.ifResponse(flag,flag,ResultCode.COMMON_FAIL);
    }


    /**
     * 更新bucketAcl
     * @param bucketName 桶名
     * @param bucketAcl bucketAcl编码
     * @return
     * @throws Exception
     */
    @PutMapping("/updateBucketAcl")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_WRITER, name = "updateBucketAcl",description = "更新bucketAcl")
    public ApiResp<Boolean> updateBucketAcl(@RequestParam(value = "bucketName") String bucketName
    ,@RequestParam(value = "bucketAcl") Integer bucketAcl) throws Exception {
        Boolean flag = bucketService.updateBucketAcl(bucketName,bucketAcl);
        return ApiResp.ifResponse(flag,flag,ResultCode.COMMON_FAIL);
    }


    /**
     * 更新Secret
     * @param bucketName 桶名
     * @param secret 加密方式对应的编码 为NULL则代表 不加密
     * @return
     * @throws Exception
     */
    @PutMapping("/updateSecret")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_WRITER, name = "updateSecret",description = "更新Secret")
    public ApiResp<Boolean> updateSecret(@RequestParam(value = "bucketName") String bucketName
            ,@RequestParam(value = "secret") Integer secret) throws Exception {
        Boolean flag = bucketService.updateSecret(bucketName,secret);
        return ApiResp.ifResponse(flag,flag,ResultCode.COMMON_FAIL);
    }

    /**
     * 更新StorageLevel
     * @param bucketName 桶名
     * @param storageLevel 存储类型对应的编码
     * @return
     * @throws Exception
     */
    @PutMapping("/updateStorageLevel")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_WRITER, name = "updateSecret",description = "更新StorageLevel")
    public ApiResp<Boolean> updateStorageLevel(@RequestParam(value = "bucketName") String bucketName
            ,@RequestParam(value = "storageLevel") Integer storageLevel) throws Exception {
        Boolean flag = bucketService.updateStorageLevel(bucketName,storageLevel);
        return ApiResp.ifResponse(flag,flag,ResultCode.COMMON_FAIL);
    }


    /**
     * 获取bucket中存在的文件类型
     * @param bucketName 桶名
     * @return
     */
    @GetMapping("/getBucketFileType")
    public ApiResp<BucketFileTypeVo> getBucketFileType(@RequestParam("bucketName")String bucketName){
        BucketFileTypeVo bucketFileType = bucketService.getBucketFileType(bucketName);
        return ApiResp.success(bucketFileType);
    }

    /**
     * 获取该用户的所有bucket中的文件类型
     * @return
     */
    @GetMapping("/getUserBucketFileType")
    public ApiResp<List<BucketFileTypeVo>> getUserBucketFileType(){
        List<BucketFileTypeVo> bucketFileTypes =  bucketService.getUserBucketFileType(JwtUtil.getID(request));
        return ApiResp.success(bucketFileTypes);
    }
}

