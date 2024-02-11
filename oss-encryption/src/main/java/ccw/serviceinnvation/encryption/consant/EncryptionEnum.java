package ccw.serviceinnvation.encryption.consant;

/**
 * 加密方式
 * @author 陈翔
 */
public enum EncryptionEnum {
    NULL(0,"NULL"),
    /**
     * SM4加密
     */
    SM4(1,"SM4"),
    /**
     * AES256加密
     */
    AES256(2,"AES256"),
    ;
    private Integer code;
    private String message;

    EncryptionEnum(Integer code, String message) {
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

    public static EncryptionEnum getEnum(Integer code) {
        for (EncryptionEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        return null;
    }

    public static EncryptionEnum getEnum(String str) {
        for (EncryptionEnum ele : values()) {
            if (ele.getMessage().equals(str)) {
                return ele;
            }
        }
        return null;
    }
}
