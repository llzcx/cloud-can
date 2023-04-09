package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户操作AccessKey的传输类
 * @author 杨世博
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
