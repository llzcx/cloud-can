package ccw.serviceinnovation.hash.etag;

import java.util.zip.Checksum;

public interface EtagHandler extends Checksum {
    String calculate(byte[] bytes);
}
