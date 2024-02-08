package ccw.serviceinnvation.encryption;

import ccw.serviceinnvation.encryption.consant.EncryptionEnum;

import java.io.IOException;

public class EncryptorFactory {

    public static EncryptEncodeHandler createEncoder(EncryptionEnum encryptionEnum) throws IOException {
       if(encryptionEnum  == EncryptionEnum.SM4){
            return new SM4EncryptHandlerImpl();
        }else{
            return null;
        }
    }

    public static EncryptDecodeHandler createDecoder(EncryptionEnum encryptionEnum) throws IOException {
        if(encryptionEnum  == EncryptionEnum.SM4){
            return new SM4DecryptHandlerImpl();
        }else{
            return null;
        }
    }
}
