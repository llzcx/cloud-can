package ccw.serviceinnovation.oss.controller;


import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
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
     * @param userId
     * @return 是否删除成功
     */
    @DeleteMapping("/deleteUser")
    public ApiResp<Boolean> deleteUser(@RequestParam("userId")Long userId) throws Exception{
        Boolean flag = manageUserService.deleteUser(userId);
        return ApiResp.ifResponse(flag,flag, ResultCode.COMMON_FAIL);
    }

    /**
     * 获取用户列表
     * @param userName
     * @param pageNum
     * @param size
     * @return
     */
    @GetMapping("/listUsers")
    public ApiResp<RPage<User>> listUsers(@RequestParam("userName")String userName,
                                          @RequestParam("pageNum")Integer pageNum,
                                          @RequestParam("size")Integer size) throws Exception{
        RPage<User> userRPage = manageUserService.getUserList(userName, pageNum, size);
        return ApiResp.success(userRPage);
    }

    /**
     * 分页获取该用户的子用户
     * @param userId
     * @param pageNum
     * @param size
     * @return
     * @throws Exception
     */
    @GetMapping("/listSubUsers")
    public ApiResp<RPage<User>> listSubUsers(@RequestParam("userId")Long userId,
                                             @RequestParam("pageNum")Integer pageNum,
                                             @RequestParam("size")Integer size) throws Exception{
        RPage<User> userRPage = manageUserService.getSubUsers(userId, pageNum, size);
        return ApiResp.success(userRPage);
    }
}
