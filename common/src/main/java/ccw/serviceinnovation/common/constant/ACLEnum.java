package ccw.serviceinnovation.common.constant;

/**
 * @author 陈翔
 */
public enum ACLEnum {

    /**
     * 公共读写：任何人都可以进行读写操作。[向公司外提供读和写,非常危险不建议设置]
     */
    PUBLIC_READ_WRITE(1,"公共读写：任何人都可以进行读写操作。"),

    /**
     * RAM读写：只有用户和子用户可以进行读写操作。 [向公司内提供桶内资源的读和写]
     */
    RAM_READ_WRITE(2,"RAM读写：用户和子用户都可以进行读写操作。"),

    /**
     * 公共读：只有用户可以进行写操作，任何人都进行读操作。 [向公司外提供桶内免费的资源的读]
     */
    PUBLIC_READ(3,"公共读：拥有者可以进行读写操作，任何人都可以进行读操作。"),

    /**
     * RAM读：只有用户进行写操作，只有子用户可以进行读操作。[向公司内提供免费的资源的写]
     */
    RAM_READ(4,"RAM读：拥有者与其子用户可以进行写操作，任何人都可以进行读操作。"),

    /**
     * 私有：只有用户可以进行读写操作，其他人包括子用户无法读写。[公司绝密文件,不提供给任何人]
     */
    PRIVATE(5,"私有：只有拥有者可以进行读写操作，其他人无法访问。"),

    DEFAULT(6,"继承bucketAcl")
    ;

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

    private Integer code;
    private String message;

    ACLEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public static ACLEnum getEnum(Integer code) {
        for (ACLEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}
