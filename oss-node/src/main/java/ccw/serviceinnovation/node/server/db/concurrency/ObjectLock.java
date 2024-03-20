package ccw.serviceinnovation.node.server.db.concurrency;

public interface ObjectLock {
    void readLock(String key);

    void readRelease(String key);

    void writeLock(String key);

    void writeRelease(String key);
}
