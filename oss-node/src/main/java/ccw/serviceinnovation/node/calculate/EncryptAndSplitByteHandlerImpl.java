package ccw.serviceinnovation.node.calculate;

import ccw.serviceinnovation.hash.etag.Crc32EtagHandlerAdapter;
import ccw.serviceinnovation.hash.etag.EtagHandler;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.split.*;
import ccw.serviceinnvation.encryption.*;
import ccw.serviceinnvation.encryption.consant.EncryptionEnum;
import org.apache.http.cookie.SM;

import java.io.IOException;
import java.security.SecureRandom;

/**
 * 字节流处理实现类 加密和拆分
 */
public class EncryptAndSplitByteHandlerImpl implements ByteHandler<byte[]> {


    @Override
    public void initialize() {
        SplitHandlerFactory.initialize(RegisterConstant.DATA_SHARDS, RegisterConstant.PARITY_SHARDS);
    }

    @Override
    public byte[][] encoder(byte[] data, EncryptionEnum encryptionEnum) throws IOException {
        EncryptEncodeHandler encryptEncodeHandler = EncryptorFactory.createEncoder(encryptionEnum);
        if(encryptEncodeHandler!=null){
            data = encryptEncodeHandler.encoder(data);
        }
        SplitEncoderHandler splitEncoderHandler = SplitHandlerFactory.createEncoder(SplitEnum.RS);
        return splitEncoderHandler.split(data);
    }

    @Override
    public byte[] decoder(byte[][] data, EncryptionEnum encryptionEnum) throws IOException {

        SplitDecoderHandler splitDecoderHandler = SplitHandlerFactory.createDecoder(SplitEnum.RS);
        byte[] merge = splitDecoderHandler.merge(data);
        EncryptDecodeHandler encryptDecodeHandler = EncryptorFactory.createDecoder(encryptionEnum);
        if(encryptDecodeHandler != null){
            return encryptDecodeHandler.decoder(merge);
        }
        throw new IOException("decode error");
    }

    public static void main(String[] args) throws IOException {
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
        byte[][] encoder = byteHandler.encoder(randomBytes, EncryptionEnum.SM4);
        byte[] decoder = byteHandler.decoder(encoder, EncryptionEnum.SM4);
        String calculate2 = etagHandler.calculate(decoder);
        System.out.println("after :" + calculate2);
    }
}
