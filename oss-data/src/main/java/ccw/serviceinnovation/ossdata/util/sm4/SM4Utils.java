package ccw.serviceinnovation.ossdata.util.sm4;


import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 陈翔
 */
@Component
public class SM4Utils {

    public String secretKey ="64EC7C763AB7BF64E2D75FF83A319918";
    public String iv;
    public boolean hexString = true;
    Pattern p = Pattern.compile("\\s*|\t|\r|\n");
    public SM4Utils() {
    }

    /**
     * ECB加密
     * @param plainText 字符串
     * @return
     */
    public String encryptData_ECB(String plainText) {
        try {
            byte[] encrypted = encryptData_ECB(plainText.getBytes("UTF-8"));
            return ByteUtil.byteToHex(encrypted);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ECB加密
     * @param bytes plainText.getBytes("UTF-8")
     * @return
     */
    public byte[] encryptData_ECB(byte[] bytes) {
        try {
            SM4Context ctx = new SM4Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            byte[] keyBytes;
            if (hexString) {
                keyBytes = ByteUtil.hexStringToBytes(secretKey);
            } else {
                //keyBytes = secretKey.getBytes();
                keyBytes = ByteUtil.hexStringToBytes(secretKey);
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_ecb(ctx, bytes);
            return encrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ECB解密
     * @param encrypted ByteUtil.hexToByte(cipherText)
     * @return
     */
    public byte[] decryptData_ECB(byte[] encrypted) {
        try {
            String cipherText;
            cipherText=Base64.encodeBase64String(encrypted);;
            if (cipherText != null && cipherText.trim().length() > 0) {

                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }

            SM4Context ctx = new SM4Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] keyBytes;
            if (hexString) {
                keyBytes = ByteUtil.hexStringToBytes(secretKey);
            } else {
                keyBytes = secretKey.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_ecb(ctx, Base64.decodeBase64(cipherText));
            return decrypted;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ECB解密
     * @param cipherText
     * @return
     */
    public String decryptData_ECB(String cipherText) {
        byte[] decrypted = decryptData_ECB(ByteUtil.hexToByte(cipherText));
        return new String(decrypted, StandardCharsets.UTF_8);
    }

    /**
     * CBC加密
     * @param plainText
     * @return
     */
    public String encryptData_CBC(String plainText) {
        return encryptData_CBC( plainText.getBytes(StandardCharsets.UTF_8));
    }
    /**
     * CBC加密
     * @param bytes plainText.getBytes("UTF-8")
     * @return
     */
    public String encryptData_CBC(byte[] bytes) {
        try {
            SM4Context ctx = new SM4Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_ENCRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString) {
                keyBytes = ByteUtil.hexStringToBytes(secretKey);
                ivBytes = ByteUtil.hexStringToBytes(iv);
            } else {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_enc(ctx, keyBytes);
            byte[] encrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, bytes);
            return ByteUtil.byteToHex(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *CBC解密
     * @param cipherText
     * @return
     */
    public String decryptData_CBC(String cipherText) {
        return decryptData_CBC(ByteUtil.hexToByte(cipherText));
    }

    /**
     *CBC解密
     * @param encrypted ByteUtil.hexToByte(cipherText)
     * @return
     */
    public String decryptData_CBC(byte[] encrypted) {
        try {
            String cipherText;
            cipherText=Base64.encodeBase64String(encrypted);
            if (cipherText != null && cipherText.trim().length() > 0) {
                Matcher m = p.matcher(cipherText);
                cipherText = m.replaceAll("");
            }
            SM4Context ctx = new SM4Context();
            ctx.isPadding = true;
            ctx.mode = SM4.SM4_DECRYPT;

            byte[] keyBytes;
            byte[] ivBytes;
            if (hexString) {
                keyBytes = ByteUtil.hexStringToBytes(secretKey);
                ivBytes = ByteUtil.hexStringToBytes(iv);
            } else {
                keyBytes = secretKey.getBytes();
                ivBytes = iv.getBytes();
            }

            SM4 sm4 = new SM4();
            sm4.sm4_setkey_dec(ctx, keyBytes);
            byte[] decrypted = sm4.sm4_crypt_cbc(ctx, ivBytes, Base64.decodeBase64(cipherText));
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取加密以后的文件大小
     * @param length
     * @return
     */
    public long getAfterSecretLength(long length){
        return Math.toIntExact(length / 16 * 16 + 16);
    }


    /**
     * SM4测试
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //948752
        //33865 73
        SM4Utils sm4 = new SM4Utils();
        String path = "D:\\OSS\\testpreview.jpg";
//        String path = "D:\\java实验9.docx";
        FileInputStream in =new FileInputStream(path);
        byte[] b;
        int size = 0;
        while (in.available() > 0) {
            size += in.available();
            b = in.available() > 1024 ? new byte[1024] : new byte[in.available()];
            in.read(b);
        }
        in.close();
        int old = size;
        System.out.println("原文:"+size);

        System.out.println("ECB模式加密");
        in =new FileInputStream(path);
        FileOutputStream fos   = new FileOutputStream("D:\\OSS\\test\\1");
        size = 0;
        while (in.available() > 0) {
            size += in.available();
            b = in.available() > 1024 ? new byte[1024] : new byte[in.available()];
            in.read(b);
            byte[] bytes =  sm4.encryptData_ECB(b);
            fos.write(bytes);
            System.out.println(bytes.length);
        }
        in.close();
        fos.flush();
        fos.close();

        in   = new FileInputStream("D:\\OSS\\test\\1");
        fos   = new FileOutputStream("D:\\OSS\\test\\2");
        size = 0;
        while (in.available() > 0) {
            size += in.available();
            b = in.available() > 1040 ? new byte[1040] : new byte[in.available()];
            in.read(b);
            byte[] bytes =  sm4.decryptData_ECB(b);
            fos.write(bytes);
            System.out.println(bytes.length);
        }
        in.close();
        fos.flush();
        fos.close();
        System.out.println("加密后:"+size);
        System.out.println("差值:"+(size-old)%1024);
    }
}
