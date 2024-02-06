package ccw.serviceinnvation.secret.consant;

/**
 * 加密方式
 * @author 陈翔
 */
public enum SecretEnum {

    NULL(0,"无加密"),
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

    SecretEnum(Integer code, String message) {
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

    public static SecretEnum getEnum(Integer code) {
        for (SecretEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}
