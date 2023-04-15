package ccw.serviceinnovation.common.util.sm4;

import cn.hutool.crypto.SecureUtil;

import java.io.File;

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

    public static void main(String[] args) {
        System.out.println(SecureUtil.md5(new File("D:\\OSS\\test\\1")));
        System.out.println(SecureUtil.md5(new File("D:\\OSS\\01\\position\\NOR&ce2ba3fb73a4312d91f4dcb65c07f4b8")));
    }
}
