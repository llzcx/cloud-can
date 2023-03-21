package ccw.serviceinnovation.common.util.sm4;

/**
 * @author 陈翔
 */
public class SM4Context {
    public int mode;

    public long[] sk;

    public boolean isPadding;

    public SM4Context()
    {
        this.mode = 1;
        this.isPadding = true;
        this.sk = new long[32];
    }

}
