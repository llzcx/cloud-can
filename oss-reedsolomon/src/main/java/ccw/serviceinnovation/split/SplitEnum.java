package ccw.serviceinnovation.split;

public enum SplitEnum {
    /**
     * SM4加密
     */
    RS(1,"SM4"),
    ;
    private Integer code;
    private String message;

    SplitEnum(Integer code, String message) {
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

    public static SplitEnum getEnum(Integer code) {
        for (SplitEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}
