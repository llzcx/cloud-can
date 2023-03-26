package ccw.serviceinnovation.oss.pojo.vo;

import lombok.Data;

/**
 * 获取对象状态的vo
 * @author 陈翔
 */
@Data
public class ObjectStateVo {
    private String bucketName;
    private String objectName;
    private String state;
}
