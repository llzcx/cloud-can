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
    Boolean deleteUser(String userId);

    /**
     * 获取用户列表
     * @param keyword
     * @param pageNum
     * @param size
     * @return
     */
    RPage<User> getUserList(String keyword, Integer pageNum, Integer size);

    /**
     * 获取该用户创建的子用户
     * @param userId
     * @param keyword
     * @param pageNum
     * @param size
     * @return
     */
    RPage<User> getSubUsers(String userId, String keyword, Integer pageNum, Integer size);
}
