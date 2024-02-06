package ccw.serviceinnovation.hash;

import java.math.BigInteger;
import java.security.MessageDigest;

public class Md5HashStrategy extends HashStrategy {

    @Override
    public String getHashString(byte[] bytes) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(bytes);
            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
