package ccw.serviceinnovation.oss.pojo.bo;

import lombok.Data;

/**
 * @author 杨世博
 * 对该存储类型的对象进行增删操作时对总大小的影响
 */
@Data
public class ObjectSizeBo {
    /**
     * 对象名字[全称]
     */
    private String name;

    /**
     * 对象的大小
     */
    private Long size;

    /**
     * 对象存储类型
     */
    private int type;

    /**
     * 删除该对象或者添加该对象
     */
    private int operatingMode;

    /**
     * 操作时间
     */
    private String operatingTime;
}
