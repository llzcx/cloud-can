package ccw.serviceinnovation.oss.pojo.dto;

import ccw.serviceinnovation.common.entity.BucketTag;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建标签的Dto
 * @author 杨世博
 */
@Data
public class PutBucketTagDto implements Serializable {

    private String bucketName;

    private List<TagDto> tags;
}
