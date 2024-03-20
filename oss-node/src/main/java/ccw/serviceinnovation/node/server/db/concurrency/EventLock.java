package ccw.serviceinnovation.node.server.db.concurrency;

public interface EventLock {
    void lock(String eventId);
    void unlock(String eventId);
}
