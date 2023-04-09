package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * AccessKey 中的基础信息
 * @author 杨世博
 */
@Data
public class MessageDto implements Serializable {

    /**
     * 创建时间
     */
    private String creationTime;

    /**
     * 有效时间
     */
    private Long survivalTime;
}
