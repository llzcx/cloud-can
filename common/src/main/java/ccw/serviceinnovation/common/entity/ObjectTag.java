package ccw.serviceinnovation.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
/**
 * <p>
 * 
 * </p>
 *
 * @author 陈翔
 * @since 2023-01-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ObjectTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对象标签唯一ID
     */
    private Long id;

    /**
     * 标签键
     */
    private String key;

    /**
     * 标签值
     */
    private String value;


}
