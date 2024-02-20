package ccw.serviceinnovation.node.index;

import ccw.serviceinnovation.hash.CRCHashStrategy;
import ccw.serviceinnovation.hash.HashStrategy;
import ccw.serviceinnovation.node.bo.ObjectMeta;
import ccw.serviceinnovation.node.leveldb.LevelDb;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class LevelDbIndexImpl implements Index {

    /**
     * 分段锁思想
     */
    private static final int SEGMENTS = 20;
    LevelDb levelDb;

    ReentrantLock[] locks = new ReentrantLock[SEGMENTS];

    HashStrategy hashStrategy = new CRCHashStrategy();

    public LevelDbIndexImpl() {
        levelDb = new LevelDb(RegisterConstant.LEVEL_DB);
        for (int i = 0; i < SEGMENTS; i++) locks[i] = new ReentrantLock();
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
        ReentrantLock lock = locks[hashStrategy.getHashCode(uniqueKey) % SEGMENTS];
        lock.lock();
        try {
            ObjectMeta objectMeta = get(uniqueKey);
            if (objectMeta == null) throw new NullPointerException("uniqueKey is null");
            Integer count = objectMeta.getCount();
            objectMeta.setCount(count + 1);
            levelDb.put(uniqueKey, objectMeta);
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean decr(String uniqueKey) {
        ReentrantLock lock = locks[hashStrategy.getHashCode(uniqueKey) % SEGMENTS];
        lock.lock();
        try {
            ObjectMeta objectMeta = get(uniqueKey);
            if (objectMeta == null) throw new NullPointerException("uniqueKey is null");
            Integer count = objectMeta.getCount();
            objectMeta.setCount(count - 1);
            levelDb.put(uniqueKey, objectMeta);
        } finally {
            lock.unlock();
        }
        return true;
    }
}
