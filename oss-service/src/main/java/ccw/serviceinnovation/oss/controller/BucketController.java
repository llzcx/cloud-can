package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.dto.AddBucketDto;
import ccw.serviceinnovation.oss.pojo.vo.FileTypeVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IBucketService;
import ccw.serviceinnovation.oss.service.IUserService;
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


    @Autowired
    IUserService userService;



    /**
     * 获取桶信息
     * @param bucketName 桶名字
     * @return 返回桶的信息
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
     * @return 分页| bucket列表数据
     * @throws Exception
     */
    @GetMapping("/listBuckets")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_LIST, name = "listBuckets",description = "获取桶列表")
    public ApiResp<RPage<Bucket>> listBuckets(Integer pageNum, Integer size, String key) throws Exception{
        log.info("{},{},{}",pageNum,size,key);
        Long mainUserId = userService.getMainUserId(JwtUtil.getID(request));
        return ApiResp.success(bucketService.getBucketList(mainUserId,pageNum,size,key));
    }

    /**
     * 创建一个桶
     * @param addBucketDto 传输对象
     * @return 返回桶的信息
     * @throws Exception
     */
    @PostMapping("/createBucket")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_WRITER, name = "createBucket",description = "创建一个桶")
    public ApiResp<Bucket> createBucket(@RequestBody AddBucketDto addBucketDto) throws Exception{
        Long mainUserId = userService.getMainUserId(JwtUtil.getID(request));
        return ApiResp.success(bucketService.createBucket(addBucketDto,mainUserId));
    }


    /**
     * 删除一个桶
     * @param bucketName 桶名
     * @return 返回是否删除成功
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
     * @return 返回是否更新成功
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
     * @return 返回是否更新成功
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
     * 获取bucket中存在的文件类型
     * @param bucketName 桶名
     * @return 返回视图对象
     */
    @GetMapping("/getBucketFileType")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_READ, name = "getBucketFileType",description = "获取bucket中存在的文件类型")
    public ApiResp<List<FileTypeVo>> getBucketFileType(@RequestParam("bucketName")String bucketName){
        List<FileTypeVo> fileTypes = bucketService.getBucketFileType(bucketName);
        return ApiResp.success(fileTypes);
    }

    /**
     * 获取该用户的所有bucket中的文件类型
     * @return 返回视图对象
     */
    @GetMapping("/getUserBucketFileType")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_READ, name = "getUserBucketFileType",description = "获取该用户的所有bucket中的文件类型")
    public ApiResp<List<FileTypeVo>> getUserBucketFileType(){
        List<FileTypeVo> bucketFileTypes =  bucketService.getUserBucketFileType(JwtUtil.getID(request));
        return ApiResp.success(bucketFileTypes);
    }

    /**
     * 桶重命名
     * @param bucketName
     * @return 返回视图对象
     */
    @PutMapping("/reName")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_WRITER, name = "reName",description = "桶重命名")
    public ApiResp<Boolean> reName(String bucketName,String newBucketName){
        return ApiResp.success(bucketService.reName(bucketName,newBucketName));
    }
}

