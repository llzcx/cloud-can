package ccw.serviceinnovation.oss.pojo.bo;

import lombok.Data;

/**
 * 用户所有存储的对象的存储类型，及其数量
 * @author 杨世博
 */
@Data
public class ObjectTypeBo {

    /**
     * 存储类型
     */
    private int type;

    /**
     * 该类型的数量
     */
    private int count;
}
