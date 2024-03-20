package ccw.serviceinnovation.node.server.db.concurrency;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class WriteEventLock implements EventLock{
    ConcurrentHashMap<String, ReentrantLock> mp = new ConcurrentHashMap<>();
    @Override
    public void lock(String eventId) {
        mp.computeIfAbsent(eventId, k -> new ReentrantLock(false)).lock();
    }

    @Override
    public void unlock(String eventId) {
        mp.computeIfAbsent(eventId, k -> new ReentrantLock(false)).unlock();
    }
}
