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
public class AuthorizeUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 授权用户id
     */
    private Long authorizeId;

    /**
     * 授权id
     */
    private Long userId;


    /**
     * 主键
     */
    private Long id;

    /**
     * 桶id
     */
    private Long bucketId;



}
