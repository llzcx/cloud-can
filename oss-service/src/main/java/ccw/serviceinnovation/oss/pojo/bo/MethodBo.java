package ccw.serviceinnovation.oss.pojo.bo;

import lombok.Data;

/**
 * 总的调用统计
 * @author 杨世博
 */
@Data
public class MethodBo {

    /**
     * 页面访问量
     */
    private int pv;

    /**
     * 独立访客数
     */
    private int uv;
}
