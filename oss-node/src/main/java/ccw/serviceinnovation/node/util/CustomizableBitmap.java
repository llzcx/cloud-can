package ccw.serviceinnovation.node.util;


import java.util.Arrays;

/**
 * bitmap实现
 * cx
 */
public class CustomizableBitmap {

    private int[] bits;

    private int size;

    private static final int EMPTY = 0x00000000;
    private static final int FULL = 0xffffffff;

    public CustomizableBitmap(int size) {
        this.size = size;
        bits = new int[upward(size)];
    }

    private int upward(int n) {
        return (n >> 5) + ((n & 31) == 0 ? 0 : 1);
    }

    public int downward(int n) {
        return n >> 5;
    }

    public  void setBit(int n, int value) {
        synchronized(this){
            if (n >= size) throw new RuntimeException("beyond the boundary.");
            if (value != 0 && value != 1) throw new RuntimeException("value error.");
            int index = downward(n);
            if (value == 1)
                bits[index] = bits[index] | (1 << (31 - n % 32));
            else
                bits[index] = bits[index] & ~(1 << (31 - n % 32));
        }
    }

    public void clear() {
        synchronized (this){
            Arrays.fill(bits, EMPTY);
        }
    }

    public int getBit(int n) {
        synchronized (this){
            if(n >= size) throw new RuntimeException("beyond the boundary.");
            int index = downward(n);
            return (bits[index] >> (31 - n % 32)) & 1;
        }
    }

    public boolean isFULL() {
        synchronized (this){
            for (int i = 0; i < bits.length - 1; i++)
                if (bits[i] != FULL) return false;
            return bits[bits.length - 1] == FULL << (32 - size % 32);
        }
    }

    public void print() {
        synchronized (this){
            for (int bit : bits) System.out.print(intToBinaryString(bit));
            System.out.println();
        }
    }

    private static String intToBinaryString(int number) {
        StringBuilder sb = new StringBuilder(32);
        for (int i = 31; i >= 0; i--) sb.append((number >> i) & 1);
        return sb.toString();
    }

    public static void main(String[] args) {
        int SIZE = 20;
        CustomizableBitmap customizableBitmap = new CustomizableBitmap(SIZE);
        for (int i = 0; i < SIZE; i++) {
            customizableBitmap.setBit(i, 1);
            customizableBitmap.print();
        }
        System.out.println(customizableBitmap.isFULL());
        System.out.println("-----getBit测试------");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(customizableBitmap.getBit(i));
        }
        System.out.println();
        System.out.println("-----------");
        for (int i = SIZE-1; i >= 0; i--) {
            customizableBitmap.setBit(i, 0);
            customizableBitmap.print();
        }
        System.out.println("------getBit测试-----");
        for (int i = 0; i < SIZE; i++) {
            System.out.print(customizableBitmap.getBit(i));
        }
        System.out.println();
        System.out.println("-----------");
    }
}
