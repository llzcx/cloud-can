package ccw.serviceinnovation.common.util.hash;

/**
 * @author 陈翔
 */
public class JdkHashCodeStrategy implements HashStrategy {

    @Override
    public int getHashCode(String origin) {
        return origin.hashCode();
    }

}
