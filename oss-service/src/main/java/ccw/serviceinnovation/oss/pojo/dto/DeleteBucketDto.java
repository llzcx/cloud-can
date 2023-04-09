package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class DeleteBucketDto implements Serializable {
    private Long bucketId;
}
