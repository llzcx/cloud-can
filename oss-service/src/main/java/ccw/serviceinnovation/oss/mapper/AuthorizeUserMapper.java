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

    /**
     * 查询bucketPolicy中其他用户
     * @param userId bucketpolicy的主用户
     * @param authorizeId bucketPolicy的id
     * @return
     */
    List<String> selectAuthorizeOtherUserList(@Param("userId") Long userId, @Param("authorizeId") Long authorizeId);

    /**
     * 查询bucketPolicy中RAM用户
     * @param userId bucketpolicy的主用户
     * @param authorizeId bucketPolicy的id
     * @return
     */
    List<String> selectAuthorizeRAMUserList(@Param("userId") Long userId, @Param("authorizeId") Long authorizeId);

}
