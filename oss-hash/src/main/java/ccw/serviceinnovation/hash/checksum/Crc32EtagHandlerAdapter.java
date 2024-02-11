package ccw.serviceinnovation.hash.checksum;

import ccw.serviceinnovation.hash.Crc32c;

import java.util.zip.Checksum;

/**
 * crc32适配器类
 */
public class Crc32EtagHandlerAdapter implements EtagHandler {

    public Crc32EtagHandlerAdapter(){
    }
    @Override
    public void update(Checksum checksum, byte[] bytes, int start, int size) {
        checksum.update(bytes,0,start);
    }

    @Override
    public String serialize(Checksum checksum) {
        Crc32c crc32c = (Crc32c) checksum;
        return String.valueOf(crc32c.getValue());
    }

    @Override
    public Checksum deserialize(String s) {
        return new Crc32c(Long.parseLong(s));
    }
}
