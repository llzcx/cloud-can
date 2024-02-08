package ccw.serviceinnvation.encryption;

import ccw.serviceinnvation.encryption.sm4.SM4Utils;

import java.io.IOException;

public class SM4DecryptHandlerImpl implements EncryptDecodeHandler {
    @Override
    public byte[] decoder(byte[] data) throws IOException {
        SM4Utils sm4Utils = new SM4Utils();
        return sm4Utils.decryptData_ECB(data);
    }
}
