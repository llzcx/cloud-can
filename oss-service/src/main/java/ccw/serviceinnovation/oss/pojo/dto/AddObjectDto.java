package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

/**用户为桶添加一个对象的dto
 * @author 陈翔
 */
@Data
public class AddObjectDto {
    private Long bucketId;
    private String objectName;
    private byte[] bytes;
}
