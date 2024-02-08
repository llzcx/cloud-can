package ccw.serviceinnvation.encryption;

import java.io.IOException;

public interface EncryptDecodeHandler {

    byte[] decoder(byte[] data) throws IOException;
}
