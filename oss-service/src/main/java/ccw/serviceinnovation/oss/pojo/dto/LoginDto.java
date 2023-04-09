package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录的Dto
 * @author 陈翔
 */
@Data
public class LoginDto implements Serializable {
    private String username;
    private String password;
}
