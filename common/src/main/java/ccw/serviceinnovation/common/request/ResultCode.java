package ccw.serviceinnovation.common.request;

/**
 * @Author: 陈翔
 * @Description: 返回码定义
 * 规定:
 * #200表示成功
 * #999表示默认失败
 * #1001～1999 区间表示参数错误
 * #2001～2999 区间表示用户错误
 * #3001～3999 区间表示接口异常
 * @Date Create in 2022/10/20 19:28
 */
public enum ResultCode {
    /* 成功 */
    SUCCESS(200, "成功"),

    ERROR_UNKNOWN(400,"未知错误"),

    ERROR_404(404,"网页或文件未找到"),

    ERROR_505(500,"出错了"),

    /* 默认失败 */
    COMMON_FAIL(999, "失败"),

    /* 参数错误：1000～1999 */
    PARAM_NOT_VALID(1001, "参数无效"),
    PARAM_IS_BLANK(1002, "参数为空"),
    PARAM_TYPE_ERROR(1003, "参数类型错误"),
    PARAM_NOT_COMPLETE(1004, "参数缺失"),

    /* 系统异常 */
    NULL_POINT_EXCEPTION(2001,"空指针异常"),
    IO_EXCEPTION(2002,"本地磁盘异常"),
    FILE_IS_EXIST(2003,"本地文件已存在"),
    DATA_BASE_ERROR(2004,"数据库异常"),
    THE_FILE_IS_CORRUPT(2005,"文件损坏"),
    DELETE_ERROR(2006,"删除失败"),
    CANT_SYNC(2007,"同步失败"),
    SERVER_EXCEPTION(2008,"数据服务异常"),
    DATA_NOT_FOUND(2009,"数据丢失"),
    UNDEFINED(2010,"不确定的后台常量"),
    SYSTEM_ERROR_DATA_NULL(2011,"系统错误-对象存储数据丢失"),
    RPC_JRAFT(2008,"RPC-JRAFT服务器异常"),
    AUTHORITY_TYPE_EXCEPTION(2010,"接口Type异常"),
    UN_KNOW_API(2011,"未知API类型,请联系管理员"),

    /* 业务异常 */
    UPLOAD_ERROR(3005,"上传失败"),
    NO_SUCH_DATA(3006,"无此数据"),
    FILE_UPLOAD_EXCEPTION(3007,"文件上传异常"),
    LOGIN_ERROR(3008,"用户不存在或者密码错误"),
    BUCKET_ACL_BLOCK(3009,"对于该桶你没有操作权限"),
    OBJECT_ACL_BLOCK(3010,"对于该桶你没有操作权限"),
    BUCKET_POLICY_BLOCK(3011,"对于该对象的路径你没有操作权限"),
    USER_NOT_LOGIN(3012, "用户未登录"),
    PERMISSION_ERROR(3013, "权限认证异常"),
    TOKEN_ERROR(3014,"令牌失效"),
    TOKEN_IS_NULL(3015,"令牌为空"),
    BUCKET_IS_DEFECT(3016,"bucket不存在"),
    OBJECT_IS_DEFECT(3017,"object不存在"),
    PARENT_OBJECT_EMPTY(3018,"父级object缺失"),
    FILE_CHECK_ERROR(3019,"文件校验异常"),
    BLOCK_TOKEN_CHECK_ERROR(3020,"分块上传事件校验异常"),
    CLIENT_ETAG_ERROR(3021,"客户端Etag计算错误"),
    FILE_IS_EMPTY(3022,"文件不存在"),
    UPLOAD_EVENT_EXPIRATION(3023,"上传事件过期或者不存在"),
    REQUEST_ADDRESS_ERROR(3024,"请求地址错误,该实现在其他服务"),
    NOT_STANDARD_STORAGE(3025,"该对象不为标准存储"),
    STANDARD_STORAGE(3025,"该对象已经为标准存储"),
    OBJECT_STATE_EXCEPTION(3026,"对象状态异常"),
    CHUNK_NOT_UP_FINISH(3027,"还有未上传完成的分块"),
    BACKUP_DATA_NULL(3028,"备份数据为空"),
    OBJECT_NAME_ERROR(3029,"对象名为NULL"),
    FILE_NAME_IS_NULL(3030,"该对象文件名为空"),
    PARENT_ID_IS_INVALID(3031,"父级文件夹无效"),
    CANT_SET_STATE(3032,"不能设置为该状态"),
    BUCKET_NAME_NULL(3033,"桶名字为空"),
    BLOCK_TOKEN_NULL(3034,"blockToken为NULL"),
    CREATE_USER_EXIST(3035,"用户名已存在"),
    BUCKET_NOT_EXIST(3036,"bucket不存在"),
    EVENT_NULL(3037,"该事件不存在"),
    NAME_IS_EXIST(3038,"该名字已经被其他对象占用"),
    FILE_IS_BIG(3039,"文件必须<=5MB,请更换上传方式(分片上传)"),
    FILE_DELETE_ERROR(3040,"批量删除终止"),
    CANT_BACKUP_BY_STORAGE(3041,"非标准存储不支持备份"),
    UPDATE_AUTHORIZE_EXCEPTION(3041,"权限更新异常"),

    ;
    private Integer code;
    private String message;

    ResultCode(Integer code, String message) {
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

    /**
     * 根据code获取message
     *
     * @param code
     * @return
     */
    public static String getMessageByCode(Integer code) {
        for (ResultCode ele : values()) {
            if (ele.getCode().equals(code)) {
                return ele.getMessage();
            }
        }
        return null;
    }
}
