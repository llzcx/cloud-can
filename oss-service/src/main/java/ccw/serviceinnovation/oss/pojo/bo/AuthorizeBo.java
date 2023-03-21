package ccw.serviceinnovation.oss.pojo.bo;

import lombok.Data;

/**
 * @author 陈翔
 */
@Data
public class AuthorizeBo {
    /**
     * 授权id
     */
    private Long authorizeId;

    /**
     * 目标是否为所有用户
     */
    private Boolean userIsAll;

    /**
     * 是否匹配所有路径
     */
    private Boolean pathIsAll;

    /**
     * 操作类型
     */
    private Integer operation;

    /**
     * 桶id
     */
    private Long bucketId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 路径
     */
    private String path;
}
