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
public class Authorize implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 授权策略唯一ID
     */
    private Long id;

    /**
     * 授权用户是否是全局
     */
    private Boolean userIsAll;

    /**
     * 授权资源是否是全部
     */
    private Boolean pathIsAll;

    /**
     * 操作
     */
    private Integer operation;


    /**
     * 桶唯一ID
     */
    private Long bucketId;


}
