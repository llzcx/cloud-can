package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 杨世博
 * @author Joy Yang
 */
@Data
public class CreateRamUserDto implements Serializable {
    private String username;
    private String password;
}
