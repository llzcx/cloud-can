package ccw.serviceinnovation.node.calculate;

import ccw.serviceinnovation.node.server.constant.RegisterConstant;
import ccw.serviceinnvation.encryption.EncryptDecodeHandler;
import ccw.serviceinnvation.encryption.EncryptEncodeHandler;
import ccw.serviceinnvation.encryption.EncryptorFactory;

import java.io.IOException;
import java.util.Objects;

public class EncryptByteHandlerImpl implements ByteHandler<byte[]> {
    @Override
    public void initialize() {

    }

    @Override
    public byte[] encoder(byte[] data) throws IOException {
        EncryptEncodeHandler encoder = EncryptorFactory.createEncoder(RegisterConstant.ENCRYPT);
        if (encoder != null) return encoder.encoder(data);
        return data;
    }

    @Override
    public byte[] decoder(byte[] data) throws IOException {
        EncryptDecodeHandler decoder = EncryptorFactory.createDecoder(RegisterConstant.ENCRYPT);
        if (decoder != null) return decoder.decoder(data);
        return data;
    }
}
