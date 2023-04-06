package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Joy Yang
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
