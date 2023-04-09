package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 陈翔
 */
@Data
public class BatchDeletionObjectDto  implements Serializable {
    private String objectNameListJson;
}
