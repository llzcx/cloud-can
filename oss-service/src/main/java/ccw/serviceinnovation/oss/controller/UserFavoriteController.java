package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.oss.service.IBucketService;
import ccw.serviceinnovation.oss.service.IUserFavoriteService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * 收藏接口
 * @author 杨世博
 */
@RestController
@RequestMapping("/favorite")
public class UserFavoriteController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    IBucketService bucketService;

    @Autowired
    IUserFavoriteService userFavoriteService;

    /**
     * 获取用户收藏的桶
     * @return 用户收藏的桶
     * @throws Exception
     */
    @GetMapping("/getUserFavorite")
    public ApiResp<List<Bucket>> getUserFavorite() throws  Exception{
        List<Bucket> bucketList = userFavoriteService.getUserFavorite(JwtUtil.getID(request));
        return ApiResp.success(bucketList);
    }

    /**
     *用户收藏一个桶
     * @param bucketName ID
     * @return 返回是否成功
     * @throws Exception
     */
    @PutMapping("/putUserFavorite")
    public ApiResp<Boolean> putUserFavorite(@RequestParam(value = "bucketName") String bucketName) throws Exception{
        Long userId = JwtUtil.getID(request);
        Boolean put = userFavoriteService.putUserFavorite(bucketName, userId);
        return ApiResp.ifResponse(put, put, ResultCode.COMMON_FAIL);
    }

    /**
     * 用户删除一个桶
     * @param bucketName
     * @return
     * @throws Exception
     */
    @DeleteMapping("/deleteUserFavorite")
    public ApiResp<Boolean> deleteUserFavorite(@RequestParam(value = "bucketName") String bucketName) throws Exception{
        Long id = JwtUtil.getID(request);
        Boolean flag = userFavoriteService.delete(id, bucketName);
        return ApiResp.ifResponse(flag,flag,ResultCode.COMMON_FAIL);
    }
}
