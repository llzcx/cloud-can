package ccw.serviceinnovation.hash;


/**
 * 陈翔
 */
public abstract class HashStrategy {
    public int getHashCode(String origin){
        throw new RuntimeException("no impl");
    }
    public byte[] getHashByte(String origin) throws Exception{
        throw new RuntimeException("no impl");
    }

}
