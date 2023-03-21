package ccw.serviceinnovation.oss.controller;


import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.request.ApiResp;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.oss.pojo.dto.LoginDto;
import ccw.serviceinnovation.oss.pojo.dto.RegisterDto;
import ccw.serviceinnovation.oss.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户接口
 * @author 陈翔
 */
@RestController
@RequestMapping("/user")
public class UserController {

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
        String token = userService.login(loginDto.getUsername(), loginDto.getPassword());
        return ApiResp.ifResponse(token!=null,token,ResultCode.LOGIN_ERROR);
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
    public ApiResp createRamUser(@RequestBody RegisterDto registerDto) {
        User user = userService.createRamUser(registerDto.getUsername(),registerDto.getPassword(),registerDto.getPhone(), JwtUtil.getID(httpServletRequest));
        return ApiResp.success(user.getId());
    }
}
