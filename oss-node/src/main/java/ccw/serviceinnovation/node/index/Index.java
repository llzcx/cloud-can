package ccw.serviceinnovation.node.index;

import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;

import java.io.IOException;

public interface Index {
    /**
     * 获取数据
     * @param etag
     * @return
     */
    ObjectMeta get(String etag);

    void add(String key, EncryptionEnum encryptionEnum);

    /**
     * 索引加载
     */
    void load() throws IOException;
}
