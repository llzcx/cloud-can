package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 标签的传输类
 * @author 杨世博
 */
@Data
public class TagDto  implements Serializable {
    private String key;
    private String value;
}
