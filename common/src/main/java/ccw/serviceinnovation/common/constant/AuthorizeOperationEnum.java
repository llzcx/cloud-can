package ccw.serviceinnovation.common.constant;


import static ccw.serviceinnovation.common.constant.AuthorityConstant.*;

/**
 * @author 陈翔
 */
public enum AuthorizeOperationEnum {

    /**
     * 只读只读（包含ListObject操作）
     */
    ONLY_READ(1,"只读（不包含ListObject操作）", new String[]{API_READ,API_LIST}),

    /**
     * 只读（包含ListObject操作）。
     */
    ONLY_READ_INCLUDE_LIST(2,"只读（包含ListObject操作）。",new String[]{API_READ}),

    /**
     * 读/写
     */
    READ_AND_WRITER(3,"读/写",new String[]{API_READ,API_LIST,API_WRITER}),

    /**
     *完全控制
     */
    FULL_CONTROL(4,"完全控制",null),

    /**
     * 拒绝访问
     */
    ACCESS_DENIED(5,"拒绝访问",null),
    ;
    private Integer code;
    private String message;
    private String[] operation;

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

    public void setOperation(String[] operation) {
        this.operation = operation;
    }

    public String[] getOperation(){
        return operation;
    }

    AuthorizeOperationEnum(Integer code, String message, String[] operation) {
        this.code = code;
        this.message = message;
        this.operation = operation;
    }
    public static AuthorizeOperationEnum getEnum(Integer code) {
        for (AuthorizeOperationEnum ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele;
            }
        }
        return null;
    }



}
