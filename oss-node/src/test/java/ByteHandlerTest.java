import ccw.serviceinnovation.hash.checksum.Crc32EtagHandlerAdapter;
import ccw.serviceinnovation.hash.checksum.EtagHandler;
import ccw.serviceinnovation.node.calculate.ByteHandler;
import ccw.serviceinnovation.node.calculate.EncryptAndSplitByteHandlerImpl;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.split.SplitHandlerFactory;
import ccw.serviceinnvation.encryption.EncryptDecodeHandler;
import ccw.serviceinnvation.encryption.EncryptEncodeHandler;
import ccw.serviceinnvation.encryption.SM4DecryptHandlerImpl;
import ccw.serviceinnvation.encryption.SM4EncryptHandlerImpl;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import org.junit.Test;

import java.io.IOException;
import java.security.SecureRandom;

public class ByteHandlerTest {
    @Test
    public void Test() throws IOException {
        RegisterConstant.ENCRYPT = EncryptionEnum.SM4;
        ByteHandler<byte[]> byteHandler = new EncryptAndSplitByteHandlerImpl();
        EncryptDecodeHandler encryptDecodeHandler = new SM4DecryptHandlerImpl();
        EncryptEncodeHandler encryptEncodeHandler = new SM4EncryptHandlerImpl();
        SplitHandlerFactory.initialize(4, 2);
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[10 * 1024];
        secureRandom.nextBytes(randomBytes);
        EtagHandler etagHandler = new Crc32EtagHandlerAdapter();
        String calculate1 = etagHandler.calculate(randomBytes);
        etagHandler.reset();
        System.out.println("before:" + calculate1);
        byte[][] encoder = byteHandler.encoder(randomBytes);
        byte[] decoder = byteHandler.decoder(encoder);
        String calculate2 = etagHandler.calculate(decoder);
        System.out.println("after :" + calculate2);
        System.out.println(calculate1.equals(calculate2));
    }
}
