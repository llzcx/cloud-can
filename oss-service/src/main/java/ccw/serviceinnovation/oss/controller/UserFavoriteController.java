package ccw.serviceinnovation.oss.controller;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.service.IBucketService;
import ccw.serviceinnovation.oss.service.IUserFavoriteService;
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
     * 获取用户收藏的桶列表
     * @return 用户收藏的桶
     * @throws Exception
     */
    @GetMapping("/getUserFavorite")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_LIST, name = "getSubUsers",description = "获取用户收藏的桶列表")
    public ApiResp<List<Bucket>> getUserFavorite() throws  Exception{
        List<Bucket> bucketList = userFavoriteService.getUserFavorite(JwtUtil.getID(request));
        return ApiResp.success(bucketList);
    }

    /**
     *用户收藏一个桶
     * @param bucketName 桶id
     * @return 返回是否成功
     * @throws Exception
     */
    @PutMapping("/putUserFavorite")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_WRITER, name = "putUserFavorite",description = "用户收藏一个桶")
    public ApiResp<List<Bucket>> putUserFavorite(@RequestParam(value = "bucketName") String bucketName) throws Exception{
        Long userId = JwtUtil.getID(request);
        List<Bucket> list = userFavoriteService.putUserFavorite(bucketName, userId);
        return ApiResp.ifResponse(list != null,list,ResultCode.BUCKET_NOT_EXIST);
    }

    /**
     * 用户删除一个桶收藏
     * @param bucketName 桶名
     * @return 删除以后的桶列表
     * @throws Exception
     */
    @DeleteMapping("/deleteUserFavorite")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_WRITER, name = "deleteUserFavorite",description = "用户删除一个桶收藏")
    public ApiResp<List<Bucket>> deleteUserFavorite(@RequestParam(value = "bucketName") String bucketName) throws Exception{
        Long id = JwtUtil.getID(request);
        List<Bucket> delete = userFavoriteService.delete(id, bucketName);
        return ApiResp.success(delete);
    }
}
