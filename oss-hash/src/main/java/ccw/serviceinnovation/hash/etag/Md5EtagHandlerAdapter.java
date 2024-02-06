package ccw.serviceinnovation.hash.etag;

import ccw.serviceinnovation.hash.HashStrategy;
import ccw.serviceinnovation.hash.Md5HashStrategy;

/**
 * md5适配器类
 */
public class Md5EtagHandlerAdapter implements EtagHandler {

    HashStrategy hashStrategy = new Md5HashStrategy();

    @Override
    public String calculate(byte[] bytes) {
        return hashStrategy.getHashString(bytes);
    }

    @Override
    public void update(int b) {
        throw new RuntimeException("no impl");
    }

    @Override
    public void update(byte[] b, int off, int len) {
        throw new RuntimeException("no impl");
    }

    @Override
    public long getValue() {
        throw new RuntimeException("no impl");
    }

    @Override
    public void reset() {
        throw new RuntimeException("no impl");
    }
}
