package ccw.serviceinnovation.node.secret;

public enum SecretEnum {

    SM4(0x01),//Sm4
    ;

    SecretEnum(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
    }

    /**
     * code转化为枚举
     */
    SecretEnum deSerialize(int code){
        SecretEnum[] values = values();
        for (SecretEnum value : values) {
            if(value.code == code){
                return value;
            }
        }
        return null;
    }


    public void setCode(Integer code) {
        this.code = code;
    }

}
