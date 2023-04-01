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
    /**
     * 是否可以正常访问
     */
    private Boolean normal;

    /**
     * 当前的状态
     */
    private String state;
}
