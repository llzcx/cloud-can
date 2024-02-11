package ccw.serviceinnovation.oss.pojo.bo;

import lombok.Data;

/**
 * @author 陈翔
 */
@Data
public class BlockTokenBo {
    /**
     * 后端对象秒传逻辑。成功则eventId为NULL
     */
    private Boolean exist;
    /**
     * 事件唯一ID
     */
    private String eventId;



}
