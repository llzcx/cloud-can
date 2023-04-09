package ccw.serviceinnovation.oss.pojo.vo;

import ccw.serviceinnovation.oss.pojo.bo.MethodBo;
import ccw.serviceinnovation.oss.pojo.bo.ObjectSizeBo;
import ccw.serviceinnovation.oss.pojo.bo.ObjectTypeBo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 向用户展示对象存储内可展示的数据
 * @author 杨世博
 */
@Data
public class AllBucketMessageVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * bucket内不同存储类型的数据大小
     * 对该存储类型的对象进行增删操作时对总大小的影响
     */
    private List<ObjectSizeBo> objectSize;

    /**
     * bucket内不同存储类型的对象数量
     */
    private List<ObjectTypeBo> objectType;

    /**
     * 调用统计
     */
    private MethodBo method;
}
