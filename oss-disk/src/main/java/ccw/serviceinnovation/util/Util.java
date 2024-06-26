package ccw.serviceinnovation.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;

public class Util {

    private static Unsafe unsafe;

    static {
        try{
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static long getAddress(Buffer buffer)  {
        try {
            Field f = Buffer.class.getDeclaredField("address");
            f.setAccessible(true);
            return (long) f.get(buffer);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyMemory(long srcAddress, long destAddress, long bytes) {
        unsafe.copyMemory(srcAddress, destAddress, bytes);
    }


    public static long assemblePosIndex(int index, long position){
        boolean negative = position < 0;
        if (negative){
            position = - position;
        }
        position |= ((long) index << 40);
        if (negative){
            position = - position;
        }
        return position;
    }

    public static long parsePos(long position){
        boolean negative = position < 0;
        if (negative){
            position = - position;
        }
        position &= 0x000000FFFFFFFFFFL;
        if (negative){
            position = - position;
        }
        return position;
    }

    public static int parseIndex(long position){
        if (position < 0){
            position = - position;
        }
        return (int) (position >>> 40);
    }

    public static int parseBits(long n, boolean unsigned) {
        int b = unsigned ? 0 : 1;
        while (n > 0){
            n >>= 1;
            b ++;
        }
        return b;
    }

    // 去掉时间戳的毫秒
    public static long trim(long timestamp){
        return timestamp / 1000 * 1000;
    }

    public static void println(Object o){
        System.out.println(System.currentTimeMillis() + " " + o);
    }

    public static void main(String[] args) {
        System.out.println(parseBits(Integer.MAX_VALUE, true));
    }

}
