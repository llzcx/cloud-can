package ccw.serviceinnovation.common.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Joy Yang
 */
@Data
public class BucketTag implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Bucket标签唯一ID
     */
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
