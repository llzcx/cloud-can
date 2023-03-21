package service.raft.rpc;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = -6597003954824547294L;
    private Boolean success;
    private Object data;
    private String msg;
    private String redirect;
    private static RpcResponse rpcResponse = new RpcResponse(true, null, null);
    public static RpcResponse getInstance(){
        return rpcResponse;
    }

    public RpcResponse() {

    }

    public RpcResponse(Boolean success, Object data, String msg) {
        this.success = success;
        this.data = data;
        this.msg = msg;
    }
    public static RpcResponse error(String msg, String redirect){
        return new RpcResponse(false, msg, redirect);
    }
    public static RpcResponse success(Object data){
        return new RpcResponse(true, data, null);
    }
}
