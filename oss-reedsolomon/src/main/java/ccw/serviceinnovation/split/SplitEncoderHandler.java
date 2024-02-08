package ccw.serviceinnovation.split;

import java.io.IOException;

public interface SplitEncoderHandler {
    byte[][] split(byte[] data) throws IOException;
}
