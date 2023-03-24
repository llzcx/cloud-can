package ccw.serviceinnovation.common.constant;

/**
 * 归档类型
 * @author 陈翔
 */
public enum StorageTypeEnum {
    /**
     * 标准存储 标准：高可靠、高可用、高性能，数据会经常被访问到。
     */
    STANDARD(1,"标准存储"),
    /**
     * 归档存储 归档：数据长期存储、基本不访问，存储单价低于低频访问型。
     */
    ARCHIVAL(3,"归档存储"),
    ;
    private Integer code;
    private String message;

    StorageTypeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static StorageTypeEnum getEnum(Integer code) {
        for (StorageTypeEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        return null;
    }
    public static StorageTypeEnum getEnum(String msg) {
        for (StorageTypeEnum ele : values()) {
            if (ele.getMessage().equals(msg)) {
                return ele;
            }
        }
        return null;
    }
}
