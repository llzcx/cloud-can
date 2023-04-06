package ccw.serviceinnovation.oss.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Joy Yang
 *
 * 后台管理的对象列表展示Vo
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ManageObjectListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 对象名字[全称]
     */
    private String name;

    /**
     * 最近更新时间
     */
    private String lastUpdateTime;

    /**
     * 存储水平
     */
    private String storageLevelString;

    /**
     * 文件总大小
     */
    private Long size;
}
