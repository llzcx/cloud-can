package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

/**
 * @author Joy Yang
 *
 * AccessKeyDto
 */
@Data
public class AccessKeyDto {

    /**
     * AccessKeyID
     */
    private Long accessKeyId;

    /**
     * 密钥
     */
    private String accessKeySecret;
}
