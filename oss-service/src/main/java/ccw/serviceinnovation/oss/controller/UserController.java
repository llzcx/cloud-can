package ccw.serviceinnovation.oss.controller;


import ccw.serviceinnovation.common.entity.Api;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
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
    public ApiResp<String> login(@RequestBody LoginDto loginDto) {
        LoginVo login = userService.login(loginDto.getUsername(), loginDto.getPassword());
        return ApiResp.ifResponse(login!=null,login,ResultCode.LOGIN_ERROR);
    }

    /**
     * 注册接口
     * @return
     */
    @PostMapping("/register")
    public ApiResp<Long> register(@RequestBody RegisterDto registerDto) {
        User user = userService.register(registerDto.getUsername(),registerDto.getPassword(),registerDto.getPhone());
        return ApiResp.success(user.getId());
    }

    /**
     * 创建一个RAM用户
     *
     * @return
     */
    @PostMapping("/createRam")
    public ApiResp createRamUser(@RequestBody CreateRamUserDto createRamUserDto) {
        User user = userService.createRamUser(createRamUserDto.getUsername(),createRamUserDto.getPassword(), JwtUtil.getID(httpServletRequest));

        System.out.println(createRamUserDto);
        return ApiResp.ifResponse(user!=null,user,ResultCode.CREATE_USER_EXIST);
    }

    /**
     * 获取子用户列表
     * @return
     */
    @GetMapping("/getSubUsers")
    public ApiResp<RPage<UserVo>> getSubUsers(@RequestParam("keyword")String keyword,
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
    public ApiResp<List<UserVo>> deleteSubUser(@RequestParam("userId")Long userId){
        List<UserVo> userVos = userService.deleteSubUser(userId, JwtUtil.getID(request));
        return ApiResp.success(userVos);
    }
}
