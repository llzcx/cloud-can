package ccw.serviceinnovation.node.bo;

import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import lombok.Data;

import java.io.Serializable;

@Data
public class ObjectMeta implements Serializable {
    private static final long serialVersionUID = -6597003954824547294L;
    /**
     * hash值
     */
    private String key;

    /**
     * 加密算法
     */
    private EncryptionEnum secret;

    /**
     * 引用数量
     */
    private Integer count;

    /**
     * 位置信息
     */
    private Position position;

    public ObjectMeta() {

    }

    public ObjectMeta(String key, EncryptionEnum secret) {
        this.key = key;
        this.secret = secret;
        count = 1;
    }


    public ObjectMeta(String key, EncryptionEnum secret,Position position) {
        this.key = key;
        this.secret = secret;
        count = 1;
        this.position = position;
    }
}
