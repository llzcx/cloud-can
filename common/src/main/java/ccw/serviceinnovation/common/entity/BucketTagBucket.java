package ccw.serviceinnovation.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 桶标签与桶的关系
 * @author 杨世博
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BucketTagBucket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * BucketID
     */
    @TableId(value = "bucket_id",type = IdType.AUTO)
    private Long bucketId;

    /**
     * 标签ID
     */
    private Long tagId;


}
