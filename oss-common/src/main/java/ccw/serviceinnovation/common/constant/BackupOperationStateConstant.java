package ccw.serviceinnovation.common.constant;

/**
 * @author 陈翔
 */
public interface BackupOperationStateConstant {
    /**
     * 读操作
     */
    byte READ = 0x32;
    /**
     * 恢操作
     */
    byte RECOVER = 0x31;

    /**
     *删除操作
     */
    byte DELETE = 0x32;
}
