package ccw.serviceinnovation.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 收藏桶
 * @author 杨世博
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserFavorite {

    /**
     * 用户唯一标识
     */
    @TableId(value = "user_id",type = IdType.AUTO)
    private Long userId;

    /**
     * 桶ID
     */
    private Long bucketId;
}
