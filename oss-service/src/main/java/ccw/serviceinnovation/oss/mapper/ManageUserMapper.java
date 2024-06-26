package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 杨世博
 */
@Mapper
public interface ManageUserMapper extends BaseMapper<User> {
    /**
     * 查询用户
     * @param offset
     * @param size
     * @param keyword
     * @param longKeyword
     * @return
     */
    List<User> selectUserListByName(Integer offset, Integer size, String keyword, Long longKeyword);

    /**
     * 查找所有用户
     * @param offset
     * @param size
     * @return
     */
    List<User> selectUserList(Integer offset, Integer size);

    /**
     * 查找所有用户的数量
     * @return
     */
    Integer selectAllCount();

    /**
     * 分页获取该用户的子用户
     * @param offset
     * @param size
     * @param userId
     * @param keyword
     * @param longKeyword
     * @return
     */
    List<User> selectSubUsers(Integer offset, Integer size, Long userId, String keyword, Long longKeyword);
}
