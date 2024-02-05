package ccw.serviceinnovation.hash;


/**
 * 陈翔
 */
public class JdkHashCodeStrategy extends HashStrategy {

    @Override
    public int getHashCode(String origin) {
        return origin.hashCode();
    }

}
