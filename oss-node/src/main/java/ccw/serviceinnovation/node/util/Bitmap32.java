package ccw.serviceinnovation.node.util;

public class Bitmap32 {
    private int bitmap;

    public Bitmap32() {
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
        Bitmap32 bitmap32 = new Bitmap32();
        bitmap32.setBit(0);
        bitmap32.setBit(2);
        bitmap32.setBit(5);
        System.out.println(bitmap32); // 输出：101001
        System.out.println(bitmap32.getBit(2)); // 输出：1
        bitmap32.clearBit(2);
        System.out.println(bitmap32); // 输出：100001
        System.out.println(bitmap32.getBit(2)); // 输出：0
    }
}
