package ccw.serviceinnovation.common.constant;

/**
 * 过期时间常量类
 * @author 陈翔
 */
public interface TimeStamp {

    /**
     * 30分钟
     */
    long ExpireTime_30MIN = 30L*60*1000;

    /**
     * 60秒
     */
    long ExpireTime_60SEC = 60L*1000;

    /**
     * 30天
     */
    long ExpireTime_1MONTH = 30L*24*60*60*1000;
}
