package ccw.serviceinnovation.node.index;

import ccw.serviceinnovation.hash.CRCHashStrategy;
import ccw.serviceinnovation.hash.HashStrategy;
import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.leveldb.LevelDb;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class LevelDbIndexImpl implements Index {

    LevelDb levelDb;

    HashStrategy hashStrategy = new CRCHashStrategy();

    public LevelDbIndexImpl() {
        levelDb = new LevelDb(RegisterConstant.LEVEL_DB);
    }

    @Override
    public ObjectMeta get(String uniqueKey) {
        return levelDb.get(uniqueKey, ObjectMeta.class);
    }

    @Override
    public void add(String uniqueKey, EncryptionEnum encryptionEnum) {
        levelDb.put(uniqueKey, new ObjectMeta(uniqueKey, encryptionEnum));
    }

    @Override
    public void load() throws IOException {
        levelDb.initLevelDB();
    }

    @Override
    public boolean incr(String uniqueKey) {
        ObjectMeta objectMeta = get(uniqueKey);
        if (objectMeta == null) return false;
        Integer count = objectMeta.getCount();
        objectMeta.setCount(count + 1);
        levelDb.put(uniqueKey, objectMeta);
        return true;
    }

    @Override
    public boolean decr(String uniqueKey) {
        ObjectMeta objectMeta = get(uniqueKey);
        if (objectMeta == null) return false;
        Integer count = objectMeta.getCount();
        if (count != 1) {
            objectMeta.setCount(count - 1);
            levelDb.put(uniqueKey, objectMeta);
        } else {
            levelDb.delete(uniqueKey);
        }
        return true;
    }

    public Map<String, ObjectMeta> iterator() {
        Map<String, ObjectMeta> map = new HashMap<>();
        List<String> keys = levelDb.getKeys();
        for (String key : keys) map.put(key, get(key));
        return map;
    }
}
