package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建子用户的Dto
 * @author 杨世博
 */
@Data
public class CreateRamUserDto implements Serializable {
    private String username;
    private String password;
}
