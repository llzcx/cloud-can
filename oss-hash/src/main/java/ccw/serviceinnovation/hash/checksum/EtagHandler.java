package ccw.serviceinnovation.hash.checksum;

import java.util.zip.Checksum;

public interface EtagHandler {
    void update(Checksum checksum,byte[] bytes,int start,int size);

    String serialize(Checksum checksum);

    Checksum deserialize(String s);
}
