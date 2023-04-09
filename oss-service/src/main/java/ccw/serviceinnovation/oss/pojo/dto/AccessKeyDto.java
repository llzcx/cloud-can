package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Joy Yang
 *
 * AccessKeyDto
 */
@Data
public class AccessKeyDto implements Serializable {

    /**
     * AccessKeyID
     */
    private Long accessKeyId;

    /**
     * 密钥
     */
    private String accessKeySecret;
}
