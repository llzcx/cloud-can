package ccw.serviceinnovation.node.index;

import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;

import java.io.IOException;

public interface Index {
    /**
     * 获取数据
     *
     * @param uniqueKey
     * @return
     */
    ObjectMeta get(String uniqueKey);

    void add(String uniqueKey, EncryptionEnum encryptionEnum);

    /**
     * 索引加载
     */
    void load() throws IOException;

    boolean incr(String uniqueKey);

    boolean decr(String uniqueKey);
}
