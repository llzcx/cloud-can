package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

/**
 * @author Joy Yang
 *
 * AccessKeyDto 的基础信息
 */
@Data
public class MessageDto {

    /**
     * 创建时间
     */
    private String creationTime;

    /**
     * 有效时间
     */
    private Long survivalTime;
}
