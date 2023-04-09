package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 注册的传输类
 * @author 陈翔
 */
@Data
public class RegisterDto implements Serializable {
    private String username;
    private String password;
    private String phone;
}
