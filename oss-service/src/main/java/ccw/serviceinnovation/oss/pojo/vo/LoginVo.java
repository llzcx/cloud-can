package ccw.serviceinnovation.oss.pojo.vo;

import lombok.Data;

/**
 * 登陆时返回的信息，token以及是否为admin的判断
 * @author 杨世博
 */
@Data
public class LoginVo {

    /**
     * 登录是返回的token
     */
    private String token;

    /**
     * 是否为管理员
     */
    private Boolean admin;
}
