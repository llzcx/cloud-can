package ccw.serviceinnovation.node.calculate;

import ccw.serviceinnovation.hash.checksum.Crc32EtagHandlerAdapter;
import ccw.serviceinnovation.hash.checksum.EtagHandler;
import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnovation.split.*;
import ccw.serviceinnvation.encryption.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.zip.Checksum;

/**
 * 字节流处理实现类 加密和拆分
 */
public class EncryptAndSplitByteHandlerImpl implements ByteHandler<byte[]> {


    @Override
    public void initialize() {
        SplitHandlerFactory.initialize(RegisterConstant.DATA_SHARDS, RegisterConstant.PARITY_SHARDS);
    }

    @Override
    public byte[][] encoder(byte[] data) throws IOException {
        EncryptEncodeHandler encryptEncodeHandler = EncryptorFactory.createEncoder(RegisterConstant.ENCRYPT);
        if(encryptEncodeHandler!=null){
            data = encryptEncodeHandler.encoder(data);
        }
        SplitEncoderHandler splitEncoderHandler = SplitHandlerFactory.createEncoder(SplitEnum.RS);
        return splitEncoderHandler.split(data);
    }

    @Override
    public byte[] decoder(byte[][] data) throws IOException {

        SplitDecoderHandler splitDecoderHandler = SplitHandlerFactory.createDecoder(SplitEnum.RS);
        byte[] merge = splitDecoderHandler.merge(data);
        EncryptDecodeHandler encryptDecodeHandler = EncryptorFactory.createDecoder(RegisterConstant.ENCRYPT);
        if(encryptDecodeHandler != null){
            merge =  encryptDecodeHandler.decoder(merge);
        }
        return merge;
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

        Checksum deserialize = etagHandler.deserialize("0");
        etagHandler.update(deserialize,randomBytes,0,randomBytes.length);
        System.out.println("before:" + etagHandler.serialize(deserialize));

        byte[][] encoder = byteHandler.encoder(randomBytes);
        byte[] decoder = byteHandler.decoder(encoder);

        deserialize = etagHandler.deserialize("0");
        etagHandler.update(deserialize,decoder,0,decoder.length);
        System.out.println("before:" + etagHandler.serialize(deserialize));
    }
}
