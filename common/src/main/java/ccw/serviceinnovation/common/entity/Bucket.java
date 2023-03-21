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
public class Bucket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 桶ID
     */
    private Long id;

    /**
     * 桶名字
     */
    private String name;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 是否开启版本控制
     */
    private Boolean versionControl;

    /**
     * 存储等级
     */
    private Integer storageLevel;

    /**
     * 图片是否需要加水印
     */
    private Boolean watermark;

    /**
     * 是否需要加密存储
     */
    private Boolean encryption;

    /**
     * 桶读写权限ACL
     */
    private Integer bucketAcl;

    /**
     * 加密方式
     */
    private String secret;


}
