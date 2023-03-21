package ccw.serviceinnovation.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author 杨世博
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserFavorite {

    /**
     * 用户唯一标识
     */
    private Long userId;

    /**
     * 桶ID
     */
    private Long bucketId;
}
