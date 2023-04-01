package ccw.serviceinnovation.common.constant;

/**
 * @author 陈翔
 */

public interface ObjectStateConstant {


    /**
     * 正常访问
     */
    Integer NOR = 1;


    /**
     * 已经归档
     */
    Integer FREEZE = 2;

    /**
     * 正在归档
     */
    Integer FREEZING = 3;


    /**
     * 正在解冻
     */
    Integer UNFREEZING = 4;
}
