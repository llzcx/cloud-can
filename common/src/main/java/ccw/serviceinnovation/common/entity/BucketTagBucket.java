package ccw.serviceinnovation.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Joy Yang
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BucketTagBucket implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * BucketID
     */
    private Long bucketId;

    /**
     * 标签ID
     */
    private Long tagId;


}
