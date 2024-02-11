package ccw.serviceinnovation.node.util;

import java.util.Iterator;

public class Bitmap {
    private int bitmap;

    public Bitmap() {
        this.bitmap = 0;
    }

    public void setBit(int index) {
        // 将指定索引的位设置为1
        this.bitmap |= (1 << index);
    }

    public void clearBit(int index) {
        // 将指定索引的位设置为0
        this.bitmap &= ~(1 << index);
    }

    public int getBit(int index) {
        // 获取指定索引的位值
        return (this.bitmap >> index) & 1;
    }
    public void setAll(){
        bitmap |= 0xffffffff;
    }

    @Override
    public String toString() {
        return Integer.toBinaryString(this.bitmap);
    }

    public static void main(String[] args) {
        Bitmap bitmap = new Bitmap();
        bitmap.setBit(0);
        bitmap.setBit(2);
        bitmap.setBit(5);
        System.out.println(bitmap); // 输出：101001
        System.out.println(bitmap.getBit(2)); // 输出：1
        bitmap.clearBit(2);
        System.out.println(bitmap); // 输出：100001
        System.out.println(bitmap.getBit(2)); // 输出：0
    }
}
