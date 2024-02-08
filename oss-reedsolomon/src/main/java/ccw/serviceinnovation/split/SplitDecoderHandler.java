package ccw.serviceinnovation.split;

import java.io.IOException;

public interface SplitDecoderHandler {
    byte[] merge(byte[][] bytes) throws IOException;
}
