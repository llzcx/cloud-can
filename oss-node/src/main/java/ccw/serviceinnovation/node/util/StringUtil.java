package ccw.serviceinnovation.node.util;


import java.io.File;
import java.util.Random;

/**
 * @author 陈翔
 */
public final class StringUtil {
    private final static String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    static Random random = new Random();

    /**
     * 获取最后一个英文点前的字符串
     *
     * @param filename
     * @return
     */
    public static String getPrefix(String filename) {
        File file = new File(filename);
        String name = file.getName();
        int index = name.lastIndexOf(".");
        if (index != -1) {
            return name.substring(0, index);
        }
        return name;
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            char randomChar = characters.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }

    public static double generateRandomDouble(double min, double max) {
        double randomValue = min + (max - min) * random.nextDouble();
        return randomValue;
    }

    public static int generateRandomInt(int min, int max) {
        Random random = new Random();
        int randomValue = random.nextInt(max - min + 1) + min;
        return randomValue;
    }

    /**
     * 获取最后2个小数点之间的字符串
     * @param inputStr
     * @return
     */
    public static String getBetweenLastTwoDots(String inputStr) {
        int lastDotIndex = inputStr.lastIndexOf('.');

        if (lastDotIndex == -1) {
            return null;  // 如果字符串中没有小数点，返回null表示无法找到满足条件的子字符串
        }

        return inputStr.substring(lastDotIndex + 1);
    }
}
