package ccw.serviceinnovation.common.exception;


import ccw.serviceinnovation.common.request.ResultCode;
import lombok.Data;

/**
 * 自定义业务异常类
 * @author 陈翔
 */

@Data
public class OssException extends RuntimeException {

    private ResultCode resultCode;
    private Integer code;
    private String msg;

    public OssException() {
        super();
    }

    public OssException(ResultCode resultCode) {
        super("{code:" + resultCode.getCode() + ",Msg:" + resultCode.getMessage() + "}");
        this.resultCode = resultCode;
        this.code = resultCode.getCode();
        this.msg = resultCode.getMessage();
    }

    public OssException(Integer code, String msg) {
        super("{code:" + code + ",Msg:" + msg + "}");
        this.code = code;
        this.msg = msg;
    }

    public OssException(Integer code, String msg, Object... args) {
        super("{code:" + code + ",Msg:" + String.format(msg, args) + "}");
        this.code = code;
        this.msg = String.format(msg, args);
    }

}
