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
public class ObjectTagObject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 对象ID
     */
    private Long objectId;

    /**
     * 标签ID
     */
    private Long tagId;


}
