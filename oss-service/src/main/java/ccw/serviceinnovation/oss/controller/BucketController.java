package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.dto.AddBucketDto;
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
     * @return 返回添加的桶对象
     */
    @PostMapping("/createBucket")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_WRITER, name = "createBucket",description = "创建一个桶")
    public ApiResp<Bucket> createBucket(@RequestBody AddBucketDto addBucketDto) throws Exception{
        return ApiResp.success(bucketService.createBucket(addBucketDto,JwtUtil.getID(request)));
    }


    /**
     * 删除一个桶
     * @return 返回是否删除成功
     */
    @DeleteMapping("/deleteBucket")
    @OssApi(target = API_BUCKET,type = AuthorityConstant.API_WRITER, name = "deleteBucket",description = "删除一个桶")
    public ApiResp<Boolean> deleteBucket(@RequestParam(value = "bucketName") String bucketName) throws Exception {
        Boolean flag = bucketService.deleteBucket(bucketName);
        return ApiResp.ifResponse(flag,flag,ResultCode.COMMON_FAIL);
    }

}

