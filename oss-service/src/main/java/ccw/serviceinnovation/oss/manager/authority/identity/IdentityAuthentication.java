package ccw.serviceinnovation.oss.manager.authority.identity;

import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.common.constant.RequestHeadersConstant;
import ccw.serviceinnovation.oss.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 身份令牌认证类
 * @author 陈翔
 */
@Component
@Slf4j
public class IdentityAuthentication {

    @Autowired
    UserMapper userMapper;

    /**
     * 验证token是否有效
     * @param token
     * @return
     */
    public User verify(String token){
        if(token==null){
            throw new OssException(ResultCode.TOKEN_IS_NULL);
        }
        String account = JwtUtil.getUsername(token);
        String secret = JwtUtil.getSecret(token);
        Long id = JwtUtil.getID(token);
        log.info("userinfo is {}",id.toString());
        User user = userMapper.selectById(id);
        log.info(user.toString());
        if(user!=null && user.getUsername().equals(account) && user.getPassword().equals(secret)) {
            return user;
        }else{
            throw new OssException(ResultCode.TOKEN_ERROR);
        }
    }

    /**
     * 验证token是否有效
     * @param request
     * @return
     */
    public User verify(HttpServletRequest request){
        String token = request.getHeader(RequestHeadersConstant.AUTHORIZATION);
        return verify(token);
    }
}
