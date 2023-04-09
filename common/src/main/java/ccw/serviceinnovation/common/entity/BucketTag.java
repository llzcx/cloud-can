package ccw.serviceinnovation.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 桶标签
 * @author 杨世博
 */
@Data
public class BucketTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Bucket标签唯一ID
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    /**
     * 标签键
     */
    private String key;

    /**
     * 标签值
     */
    private String value;
}
