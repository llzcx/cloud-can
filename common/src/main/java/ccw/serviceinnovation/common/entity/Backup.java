package ccw.serviceinnovation.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class Backup implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 源对象Id
     */
    private Long sourceObjectId;

    /**
     * 源bucketId
     */
    private Long targetObjectId;

    /**
     * 创建时间
     */
    private String createTime;
}
