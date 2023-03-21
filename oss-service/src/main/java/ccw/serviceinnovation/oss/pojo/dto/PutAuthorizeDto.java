package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

/**
 * 添加bucket权限的传输类
 * @author 陈翔
 */
@Data
public class PutAuthorizeDto {
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
     * 路径列表
     */
    String[] paths;

    /**
     * 是否包含了所有用户
     */
    Boolean isAllUser;

    /**
     * 子用户
     */
    String[] sonUser;

    /**
     * 其他用户
     */
    String[] otherUser;


}
