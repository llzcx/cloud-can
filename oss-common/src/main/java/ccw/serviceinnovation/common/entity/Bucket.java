package ccw.serviceinnovation.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(value = "id",type = IdType.AUTO)
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
     * 桶读写权限ACL
     */
    private Integer bucketAcl;


}
