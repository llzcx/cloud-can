package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.oss.pojo.vo.LoginVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.pojo.vo.UserVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
    LoginVo login(String username, String password);

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
     * @param userId 所属用户
     * @return
     */
    User createRamUser(String username, String password,Long userId);

    /**
     * 获取子用户列表
     * @param id
     * @param keyword
     * @return
     */
    RPage<UserVo> getSubUsers(Long id, String keyword, Integer pageNum, Integer size);

    /**
     * 删除子用户
     * @param userId
     * @param myId
     * @return
     */
    List<UserVo> deleteSubUser(Long userId, Long myId);
}
