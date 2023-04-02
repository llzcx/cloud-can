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
public class Api implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 接口名字
     */
    private String name;

    /**
     * 接口描述
     */
    private String description;
}
