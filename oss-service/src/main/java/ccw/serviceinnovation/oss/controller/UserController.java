package ccw.serviceinnovation.oss.controller;


import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.Api;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.manager.authority.OssApi;
import ccw.serviceinnovation.oss.pojo.dto.CreateRamUserDto;
import ccw.serviceinnovation.oss.pojo.dto.LoginDto;
import ccw.serviceinnovation.oss.pojo.dto.RegisterDto;
import ccw.serviceinnovation.oss.pojo.vo.LoginVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.pojo.vo.UserVo;
import ccw.serviceinnovation.oss.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ccw.serviceinnovation.common.constant.AuthorityConstant.API_OPEN;

/**
 * 用户接口
 * @author 陈翔
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    IUserService userService;

    @Autowired(required = false)
    private HttpServletRequest httpServletRequest;

    /**
     * 登录接口
     * @param loginDto 登录参数
     * @return
     */
    @PostMapping("/login")
    @OssApi(target = AuthorityConstant.API_OPEN,type = AuthorityConstant.API_WRITER, name = "login",description = "登录接口")
    public ApiResp<String> login(@RequestBody LoginDto loginDto) {
        LoginVo login = userService.login(loginDto.getUsername(), loginDto.getPassword());
        return ApiResp.ifResponse(login!=null,login,ResultCode.LOGIN_ERROR);
    }

    /**
     * 注册接口
     * @return
     */
    @PostMapping("/register")
    @OssApi(target = AuthorityConstant.API_OPEN,type = AuthorityConstant.API_WRITER, name = "register",description = "注册接口")
    public ApiResp<Long> register(@RequestBody RegisterDto registerDto) {
        User user = userService.register(registerDto.getUsername(),registerDto.getPassword(),registerDto.getPhone());
        return ApiResp.ifResponse(user!=null,user,ResultCode.CREATE_USER_EXIST);
    }

    /**
     * 创建一个RAM用户
     * @return
     */
    @PostMapping("/createRam")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_WRITER, name = "createRam",description = "创建一个RAM用户")
    public ApiResp createRamUser(@RequestBody CreateRamUserDto createRamUserDto) {
        User user = userService.createRamUser(createRamUserDto.getUsername(),createRamUserDto.getPassword(), JwtUtil.getID(httpServletRequest));

        System.out.println(createRamUserDto);
        return ApiResp.ifResponse(user!=null,user,ResultCode.CREATE_USER_EXIST);
    }

    /**
     * 更新密码
     * @param username
     * @param password
     * @return
     */
    @PutMapping("/update")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_WRITER, name = "update",description = "更新密码")
    public ApiResp update(String username,String password) {
        User user = new User();
        user.setPassword(password);
        userService.update(user, MPUtil.queryWrapperEq("username",username));
        return ApiResp.success(true);
    }
    /**
     * 获取子用户列表
     * @param keyword 搜索关键词
     * @param pageNum 当前页数
     * @param size 每页展示条数
     * @return
     */
    @GetMapping("/getSubUsers")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_LIST, name = "getSubUsers",description = "获取子用户列表")
    public ApiResp<RPage<UserVo>> getSubUsers(@RequestParam(value = "keyword",required = false)String keyword,
                                      @RequestParam("pageNum")Integer pageNum,
                                      @RequestParam("size")Integer size){
        RPage<UserVo> userVos = userService.getSubUsers(JwtUtil.getID(request),keyword,pageNum,size);
        return ApiResp.success(userVos);
    }

    /**
     * 删除子用户
     * @param userId 子用户id
     * @return
     */
    @DeleteMapping("/deleteSubUser")
    @OssApi(target = AuthorityConstant.API_USER,type = AuthorityConstant.API_WRITER, name = "deleteSubUser",description = "删除子用户")
    public ApiResp<List<UserVo>> deleteSubUser(@RequestParam("userId")Long userId){
        List<UserVo> userVos = userService.deleteSubUser(userId, JwtUtil.getID(request));
        return ApiResp.success(userVos);
    }
}
