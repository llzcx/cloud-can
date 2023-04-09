package ccw.serviceinnovation.oss.pojo.dto;

import ccw.serviceinnovation.common.entity.BucketTag;
import ccw.serviceinnovation.common.entity.ObjectTag;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 删除对象标签的Dto
 * @author 杨世博
 */
@Data
public class DeleteObjectTagDto implements Serializable{

    private String bucketName;

    private String objectName;

    private List<ObjectTag> objectTags;
}
