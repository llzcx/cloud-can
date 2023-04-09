package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Joy Yang
 */
@Data
public class TagDto  implements Serializable {
    private String key;
    private String value;
}
