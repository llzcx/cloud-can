package ccw.serviceinnovation.oss.common.util;

import ccw.serviceinnovation.common.constant.RequestHeadersConstant;

import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.StringUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: LEAVES
 * @Date: 2020年12月30日 14时25分08秒
 * @Version 1.0
 * @Description:
 */
@Slf4j
public class JwtUtil {

    /**
     * token过期时间     5小时
     */
    private static final long EXPIRE_TIME = 1000 * 60 * 60 * 5;

    /**
     * redis中token过期时间   12小时
     */
    public static final Integer REFRESH_EXPIRE_TIME = 60 * 60 * 12;

    /**
     * token密钥(自定义)
     */
    private static final String TOKEN_SECRET = "^/zxc*123!@#$%^&*/";

    private static final String ID = "id";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";
    private static final String CURRENT_TIME = "currentTime";

    /**
     * 校验token是否正确
     * @param token token
     * @param username 用户名
     * @return 是否正确
     */
    public static boolean verify(String token, String username){
        log.info("JwtUtil==verify--->");
        try {
            log.info("JwtUtil==verify--->校验token是否正确");
            //根据密码生成JWT效验器
            //秘钥是密码则省略
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim(USERNAME, username)
//                    .withClaim("secret",secret)  //秘钥是密码直接传入
                    .build();
            //效验TOKEN
            DecodedJWT jwt = verifier.verify(token);
            log.info("JwtUtil==verify--->jwt = "+jwt.toString());
            log.info("JwtUtil==verify--->JwtUtil验证token成功!");
            return true;
        }catch (Exception e){
            log.error("JwtUtil==verify--->JwtUtil验证token失败!");
            return false;
        }
    }

    /**
     * 获取token中的信息（包含用户名）
     * @param token
     * @return
     */
    public static String getUsername(String token) {
        if (StringUtil.isBlank(token)){
            throw new OssException(ResultCode.TOKEN_ERROR);
        }
        try {
            DecodedJWT jwt = JWT.decode(token);
            log.info("token = " + jwt.getToken());
            return jwt.getClaim(USERNAME).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 生成token签名
     * EXPIRE_TIME 分钟后过期
     * @param username 用户名
     * @return 加密的token
     */
    public static String sign(Long id,String username,String password) {
        log.info("JwtUtil==sign--->");
        Map<String, Object> header = new HashMap<>();
        header.put("type","Jwt");
        header.put("alg","HS256");
        long currentTimeMillis = System.currentTimeMillis();
        //设置token过期时间
        Date date = new Date(currentTimeMillis + EXPIRE_TIME);
        //秘钥是密码则省略
        Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
        //生成签名
        AtomicInteger
        String sign = JWT.create()
                .withHeader(header)
                .withExpiresAt(date)
                .withClaim(USERNAME, username)
                .withClaim(PASSWORD,password)
                .withClaim(ID,id.toString())
                .withClaim(CURRENT_TIME, currentTimeMillis + EXPIRE_TIME)
                .sign(algorithm);
        log.info("JwtUtil==sign--->sign = " + sign);
        return sign;
    }
    /**
     * 从请求头获取token在解析出ID
     * @param httpServletRequest
     * @return
     */
    public static Long getID(HttpServletRequest httpServletRequest){
        String token = httpServletRequest.getHeader(RequestHeadersConstant.AUTHORIZATION);
        return getID(token);
    }

    /**
     * 获取token中用户ID
     * @param token
     * @return
     */
    public static Long getID(String token){
        try{
            DecodedJWT decodedJWT=JWT.decode(token);
            String sid = decodedJWT.getClaim(ID).asString();
            return Long.valueOf(sid);
        }catch (JWTCreationException e){
            return null;
        }
    }

    /**
     * 获取token中用户密码
     * @param token
     * @return
     */
    public static String getSecret(String token){
        try{
            DecodedJWT decodedJWT=JWT.decode(token);
            return decodedJWT.getClaim(PASSWORD).asString();
        }catch (JWTCreationException e){
            return null;
        }
    }

    /**
     * 获取token的时间戳
     * @param token
     * @return
     */
    public static Long getCurrentTime(String token){
        try{
            DecodedJWT decodedJWT=JWT.decode(token);
            return decodedJWT.getClaim(CURRENT_TIME).asLong();

        }catch (JWTCreationException e){
            return null;
        }
    }


    public static void main(String[] args) {
        Long value = JwtUtil.getID("eyJ0eXBlIjoiSnd0IiwiYWxnIjoiSFMyNTYiLCJ0eXAiOiJKV1QifQ.eyJjdXJyZW50VGltZSI6MTY3NTQ1MzE5MzEyMywicGFzc3dvcmQiOiIxMjMiLCJpZCI6IjEiLCJleHAiOjE2NzU0NTMxOTMsInVzZXJuYW1lIjoiMTIzIn0.vLR7BdJ91p15sz6vh814MU4CZUm7FhqvP7YsBwGxNcA");
        System.out.println("value = " + value);
    }

}