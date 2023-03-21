package ccw.serviceinnovation.common.constant;

/**
 *BucketACL常量
 * @author 陈翔
 */
public enum BucketACLEnum {

    /**
     * 公共读写：任何人都可以对该Bucket内文件进行读写操作。[向公司外提供桶内资源的读和写]
     */
    PUBLIC_READ_WRITE(1,"公共读写：任何人都可以对该Bucket内文件进行读写操作。"),

    /**
     * RAM读写：用户和子用户都可以对该Bucket内文件进行读写操作。 [向公司内提供桶内资源的读和写]
     */
    RAM_READ_WRITE(2,"RAM读写：用户和子用户都可以对该Bucket内文件进行读写操作。"),

    /**
     * 公共读：只有该Bucket的拥有者可以对该Bucket内的文件进行写操作，任何人都可以对该Bucket中的文件进行读操作。 [向公司外提供桶内免费的资源的读]
     */
    PUBLIC_READ(3,"公共读：只有该Bucket的拥有者可以对该Bucket内的文件进行写操作，任何人都可以对该Bucket中的文件进行读操作。"),

    /**
     * RAM读：只有该Bucket的拥有者与其子用户可以对该Bucket内的文件进行写操作，任何人都可以对该Bucket中的文件进行读操作。[向公司内提供桶内免费的资源的写]
     */
    RAM_READ(4,"RAM读：只有该Bucket的拥有者与其子用户可以对该Bucket内的文件进行写操作，任何人都可以对该Bucket中的文件进行读操作。"),

    /**
     * 私有：只有Bucket的拥有者可以对该Bucket内的文件进行读写操作，其他人无法访问该Bucket内的文件。[公司绝密文件,不想任何人提供]
     */
    PRIVATE(5,"私有：只有Bucket的拥有者可以对该Bucket内的文件进行读写操作，其他人无法访问该Bucket内的文件。"),


    ;
    private Integer code;
    private String message;

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

    BucketACLEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
    public static BucketACLEnum getEnum(Integer code) {
        for (BucketACLEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        return null;
    }
}
