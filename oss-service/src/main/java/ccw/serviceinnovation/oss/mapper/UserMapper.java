package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 陈翔
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 搜索获取 user
     * @param id
     * @param keyword
     * @return
     */
    List<User> selectUsersByName(Long id, String keyword,Integer offset, Integer size);

    Integer selectCountByName(Long id, String keyword);
}
