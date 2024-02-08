package ccw.serviceinnvation.encryption;

import java.io.IOException;

public interface EncryptEncodeHandler {

    byte[] encoder(byte[] data) throws IOException;
}
