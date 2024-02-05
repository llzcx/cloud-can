package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.UserFavorite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @author 杨世博
 */
@Mapper
@Repository
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

    /**
     * 先判断是否存在该收藏，不存在增加字段添加收藏，存在更新字段
     * @param userFavorite 收藏内容
     * @return
     */
    Long putUserFavorite(UserFavorite userFavorite);
}
