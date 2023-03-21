package ccw.serviceinnovation.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * 字符串相关工具类
 * @Author: 陈翔
 * @Date: 2022年10月05日
 * @Version 1.0
 * @Description:   String工具
 */
@Component
@Slf4j
public class StringUtil {

    private StringUtil() {}

    /**
     * 定义下划线
     */
    private static final char UNDERLINE = '_';

    /**
     * String为空判断(不允许空格)
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        return str == null || "".equals(str.trim());
    }

    /**
     * String不为空判断(不允许空格)
     * @param str
     * @return
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * Byte数组为空判断
     * @param bytes
     * @return boolean
     */
    public static boolean isNull(byte[] bytes) {
        // 根据byte数组长度为0判断
        return bytes == null || bytes.length == 0;
    }

    /**
     * Byte数组不为空判断
     * @param bytes
     * @return boolean
     */
    public static boolean isNotNull(byte[] bytes) {
        return !isNull(bytes);
    }

    /**
     * 驼峰转下划线工具
     * @param param
     * @return java.lang.String
     */
    public static String camelToUnderline(String param) {
        if (isNotBlank(param)) {
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char c = param.charAt(i);
                if (Character.isUpperCase(c)) {
                    sb.append(UNDERLINE);
                    sb.append(Character.toLowerCase(c));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * 下划线转驼峰工具
     * @param param
     * @return java.lang.String
     */
    public static String underlineToCamel(String param) {
        if (isNotBlank(param)) {
            int len = param.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char c = param.charAt(i);
                if (c == 95) {
                    i++;
                    if (i < len) {
                        sb.append(Character.toUpperCase(param.charAt(i)));
                    }
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * 在字符串两周添加''
     * @param param
     * @return java.lang.String
     */
    public static String addSingleQuotes(String param) {
        return "\'" + param + "\'";
    }


    /**
     * 生成一个长度为lenth的字符串(有数字和英文)
     * @param length
     * @return
     */
    public static String getCharAndNum(int length) {

        Random random = new Random();

        StringBuffer valSb = new StringBuffer();

        String charStr = "0123456789abcdefghijklmnopqrstuvwxyz";

        int charLength = charStr.length();



        for (int i = 0; i < length; i++) {

            int index = random.nextInt(charLength);

            valSb.append(charStr.charAt(index));

        }

        return valSb.toString();

    }

    /**
     * 生成一个长度为lenth的数字串
     * @param length
     * @return
     */
    public static String getNum(int length) {

        Random random = new Random();

        StringBuffer valSb = new StringBuffer();

        String charStr = "0123456789";

        int charLength = charStr.length();



        for (int i = 0; i < length; i++) {

            int index = random.nextInt(charLength);

            valSb.append(charStr.charAt(index));

        }

        return valSb.toString();
    }

    /**
     * s_ISO_8859_1转换成UTF-8
     * @param s_ISO_8859_1
     * @return
     */
    public static String getUTF8(String s_ISO_8859_1){
        if(s_ISO_8859_1==null){ return null;}
        return new String(s_ISO_8859_1.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }


    /**
     * 判断字符串数组当中某个字符串是否存在
     * @param stringList
     * @param t
     * @return
     */
    public static Boolean findString(List<String> stringList, String t){
        for (String s:stringList) {
            if (s.equals(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断字符串是否是 纯数字串并且长度不超过 限定的最大值数的长度--过渡用
     * @param s
     * @param len
     * @return
     */
    public static boolean isNum(String s,int len)
    {
        Pattern p=Pattern.compile("^[1-9]{1,"+len+"}$");
        return p.matcher(s).matches();
    }
}