package ccw.serviceinnovation.node.calculate;

import ccw.serviceinnvation.encryption.consant.EncryptionEnum;

import java.io.IOException;

/**
 * 字节流处理
 *
 * @param <T>
 */
public interface ByteHandler<T> {
    void initialize();

    T[] encoder(byte[] data, EncryptionEnum encryptionEnum) throws IOException;

    byte[] decoder(T[] data, EncryptionEnum encryptionEnum) throws IOException;
}
