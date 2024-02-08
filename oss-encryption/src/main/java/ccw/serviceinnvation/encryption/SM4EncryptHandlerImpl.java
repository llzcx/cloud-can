package ccw.serviceinnvation.encryption;

import ccw.serviceinnvation.encryption.sm4.SM4Utils;

import java.io.IOException;

public class SM4EncryptHandlerImpl implements EncryptEncodeHandler {

    @Override
    public byte[] encoder(byte[] data) throws IOException {
        SM4Utils sm4Utils = new SM4Utils();
        return sm4Utils.encryptData_ECB(data);
    }
}
