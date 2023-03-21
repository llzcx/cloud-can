package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.Authorize;
import ccw.serviceinnovation.oss.pojo.bo.AuthorizeBo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 陈翔
 */
@Mapper
public interface AuthorizeMapper  extends BaseMapper<Authorize> {
    /**
     *从数据库获取所有的拒绝的路径
     * @param userId
     * @param bucketId
     * @param path
     * @return
     */
    List<AuthorizeBo> selectAuthorize(@Param("userId") Long userId, @Param("bucketId") Long bucketId, @Param("path") String path);
}
