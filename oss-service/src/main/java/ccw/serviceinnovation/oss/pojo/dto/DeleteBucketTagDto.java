package ccw.serviceinnovation.oss.pojo.dto;

import ccw.serviceinnovation.common.entity.BucketTag;
import ccw.serviceinnovation.common.entity.ObjectTag;
import lombok.Data;

import java.util.List;

/**
 * @author Joy Yang
 */
@Data
public class DeleteBucketTagDto {

    /**
     *
     */
    private String bucketName;

    /**
     *
     */
    private List<BucketTag> bucketTags;
}
