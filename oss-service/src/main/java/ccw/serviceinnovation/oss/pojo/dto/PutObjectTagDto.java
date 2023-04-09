package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 添加对象标签的dto
 * @author 杨世博
 */
@Data
public class PutObjectTagDto implements Serializable {

    /**
     *
     */
    private String bucketName;

    /**
     *
     */
    private String objectName;

    /**
     *
     */
    private List<TagDto> objectTags;
}
