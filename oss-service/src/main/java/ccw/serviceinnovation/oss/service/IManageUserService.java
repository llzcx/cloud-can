package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 杨世博
 */
public interface IManageUserService extends IService<User> {

    /**
     * 删除用户
     * @param userId
     * @return
     */
    Boolean deleteUser(Long userId);

    /**
     * 获取用户列表
     * @param userName
     * @param pageNum
     * @param size
     * @return
     */
    RPage<User> getUserList(String userName, Integer pageNum, Integer size);

    /**
     * 获取该用户创建的子用户
     * @param userId
     * @param pageNum
     * @param size
     * @return
     */
    RPage<User> getSubUsers(Long userId, Integer pageNum, Integer size);
}
