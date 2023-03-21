package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 陈翔
 */
public interface IUserService extends IService<User> {
    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    String login(String username, String password);

    /**
     * 注册账号
     * @param username 用户名
     * @param password 密码
     * @param phone 手机号
     * @return
     */
    User register(String username, String password,String phone);


    /**
     * 创建RAM用户
     * @param username
     * @param password
     * @param phone
     * @param userId 所属用户
     * @return
     */
    User createRamUser(String username, String password, String phone,Long userId);



}
