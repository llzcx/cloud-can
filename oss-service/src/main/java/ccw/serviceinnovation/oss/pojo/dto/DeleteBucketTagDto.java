package ccw.serviceinnovation.oss.pojo.dto;

import ccw.serviceinnovation.common.entity.BucketTag;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 删除bucket标签的Dto
 * @author 杨世博
 */
@Data
public class DeleteBucketTagDto implements Serializable {

    private String bucketName;

    private List<BucketTag> bucketTags;
}
