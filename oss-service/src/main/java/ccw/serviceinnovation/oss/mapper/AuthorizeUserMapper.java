package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.AuthorizeUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 陈翔
 */
@Mapper
public interface AuthorizeUserMapper extends BaseMapper<AuthorizeUser> {

    List<AuthorizeUser> selectAuthorizeMainUserList(@Param("userId") Long userId,@Param("authorizeId") Long authorizeId);

    List<AuthorizeUser> selectAuthorizeRAMUserList(@Param("userId") Long userId,@Param("authorizeId") Long authorizeId);

}
