package ccw.serviceinnovation.hash.directcalculator;

import ccw.serviceinnovation.hash.Md5HashStrategy;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5EtagDirectCalculatorAdapter implements EtagDirectCalculator {

    @Override
    public String get(byte[] data) {
        Md5HashStrategy md5HashStrategy = new Md5HashStrategy();
        return md5HashStrategy.getHashString(data);
    }

    @Override
    public String get(Path path) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            while (channel.read(buffer) != -1) {
                buffer.flip();
                md.update(buffer);
                buffer.clear();
            }
            return new BigInteger(1, md.digest()).toString(16);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
