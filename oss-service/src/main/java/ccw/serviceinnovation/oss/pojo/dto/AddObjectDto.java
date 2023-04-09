package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户为桶添加一个对象的dto
 * @author 陈翔
 */
@Data
public class AddObjectDto  implements Serializable {
    private Long bucketId;
    private String objectName;
    private byte[] bytes;
}
