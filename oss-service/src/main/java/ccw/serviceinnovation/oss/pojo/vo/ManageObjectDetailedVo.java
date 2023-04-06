package ccw.serviceinnovation.oss.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Joy Yang
 *
 * 后台管理的详细对象展示Vo
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ManageObjectDetailedVo implements Serializable {

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
     * 文件的MD5值
     */
    private String etag;

    /**
     * 最近更新时间
     */
    private String lastUpdateTime;

    /**
     * 当前的状态
     */
    private String state;

    /**
     * 存储水平
     */
    private String storageLevelString;

    /**
     * 对象访问控制
     */
    private String objectAclString;
}
