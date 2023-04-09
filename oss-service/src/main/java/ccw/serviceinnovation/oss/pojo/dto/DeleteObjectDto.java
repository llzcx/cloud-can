package ccw.serviceinnovation.oss.pojo.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 删除对象的Dto
 * @author 陈翔
 */
@Data
public class DeleteObjectDto  implements Serializable {
    private Long objectId;
}
