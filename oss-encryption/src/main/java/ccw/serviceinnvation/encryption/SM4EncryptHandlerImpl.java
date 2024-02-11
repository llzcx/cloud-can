package ccw.serviceinnvation.encryption;

import ccw.serviceinnvation.encryption.sm4.SM4Utils;

import java.io.IOException;

public class SM4EncryptHandlerImpl implements EncryptEncodeHandler {

    SM4Utils sm4Utils = new SM4Utils();
    @Override
    public byte[] encoder(byte[] data) throws IOException {
        return sm4Utils.encryptData_ECB(data);
    }
}
