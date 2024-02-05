package ccw.serviceinnovation.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 接口信息
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

    /**
     * 接口类型
     */
    private String type;

    /**
     * 接口的目标
     */
    private String target;
}
