package ccw.serviceinnovation.hash.etag;

import ccw.serviceinnovation.hash.Crc32c;

/**
 * crc32适配器类
 */
public class Crc32EtagHandlerAdapter implements EtagHandler{

    Crc32c crc32 = new Crc32c();
    @Override
    public String calculate(byte[] bytes) {
        update(bytes,0,bytes.length);
        return String.valueOf(getValue());
    }

    @Override
    public void update(int b) {
        crc32.update(b);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        crc32.update(b,off,len);
    }

    @Override
    public long getValue() {
        return crc32.getValue();
    }

    @Override
    public void reset() {
        crc32.reset();
    }
}
