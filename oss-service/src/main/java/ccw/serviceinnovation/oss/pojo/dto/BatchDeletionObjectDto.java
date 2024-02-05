package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 对象名集合的Json
 * @author 陈翔
 */
@Data
public class BatchDeletionObjectDto  implements Serializable {
    /**
     * 对象名列表 json数据
     */
    private String objectNameListJson;
}
