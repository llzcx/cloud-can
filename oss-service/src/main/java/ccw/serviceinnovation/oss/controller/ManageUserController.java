package ccw.serviceinnovation.oss.controller;


import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 管理用户接口
 * @author 杨世博
 */
@RestController
@RequestMapping("/manageUser")
public class ManageUserController {

    @Autowired
    IManageUserService manageUserService;

    /**
     * 删除用户
     * @param userId 用户id
     * @return 是否删除成功
     */
    @DeleteMapping("/deleteUser")
    @OssApi(target = AuthorityConstant.API_MANAGE,type = AuthorityConstant.API_WRITER, name = "deleteUser",description = "删除用户")
    public ApiResp<Boolean> deleteUser(@RequestParam("userId")String userId) throws Exception{
        Boolean flag = manageUserService.deleteUser(userId);
        return ApiResp.ifResponse(flag,flag, ResultCode.COMMON_FAIL);
    }

    /**
     * 获取用户列表
     * @param keyword 用户名，用户id
     * @param pageNum 当前页数
     * @param size 每页数据条数
     * @return
     */
    @GetMapping("/listUsers")
    @OssApi(target = AuthorityConstant.API_MANAGE,type = AuthorityConstant.API_READ, name = "listUsers",description = "获取用户列表")
    public ApiResp<RPage<User>> listUsers(@RequestParam("keyword")String keyword,
                                          @RequestParam("pageNum")Integer pageNum,
                                          @RequestParam("size")Integer size) throws Exception{
        RPage<User> userRPage = manageUserService.getUserList(keyword, pageNum, size);
        return ApiResp.success(userRPage);
    }

    /**
     * 分页获取该用户的子用户
     * @param userId 主用户id
     * @param keyword 用户名，用户id
     * @param pageNum 当前页数
     * @param size 每页数据条数
     * @return
     * @throws Exception
     */
    @GetMapping("/listSubUsers")
    @OssApi(target = AuthorityConstant.API_MANAGE,type = AuthorityConstant.API_READ, name = "listSubUsers",description = "分页获取该用户的子用户")
    public ApiResp<RPage<User>> listSubUsers(@RequestParam("userId")String userId,
                                             @RequestParam("keyword")String keyword,
                                             @RequestParam("pageNum")Integer pageNum,
                                             @RequestParam("size")Integer size) throws Exception{
        RPage<User> userRPage = manageUserService.getSubUsers(userId, keyword, pageNum, size);
        return ApiResp.success(userRPage);
    }

}
