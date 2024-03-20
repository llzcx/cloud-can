package ccw.serviceinnovation.node.server.db.concurrency;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 */
public class ObjectThreadBlockReadWriteLock implements ObjectLock {

    ConcurrentHashMap<String, ReentrantReadWriteLock> mp = new ConcurrentHashMap<>();

    @Override
    public void readLock(String key) {
        mp.computeIfAbsent(key, k -> new ReentrantReadWriteLock(false)).readLock().lock();
    }

    @Override
    public void readRelease(String key) {
        mp.computeIfAbsent(key, k -> new ReentrantReadWriteLock(false)).readLock().unlock();
    }

    @Override
    public void writeLock(String key) {
        mp.computeIfAbsent(key, k -> new ReentrantReadWriteLock(false)).writeLock().lock();
    }

    @Override
    public void writeRelease(String key) {
        mp.computeIfAbsent(key, k -> new ReentrantReadWriteLock(false)).writeLock().unlock();
    }
}
