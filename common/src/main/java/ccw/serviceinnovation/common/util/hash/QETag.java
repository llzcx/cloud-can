package ccw.serviceinnovation.common.util.hash;

/*
java 最新版本
https://github.com/qiniu/java-sdk/blob/master/src/main/java/com/qiniu/util/Etag.java
android 最新版本
https://github.com/qiniu/android-sdk/blob/master/library/src/main/java/com/qiniu/android/utils/Etag.java
*/

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;


/**
 * qetag
 * qetag 是一个计算文件在七牛云存储上的 hash 值（也是文件下载时的 etag 值）的实用程序。
 *
 * 七牛的 hash/etag 算法是公开的。算法大体如下：
 *
 * 如果你能够确认文件 <= 4M，那么 hash = UrlsafeBase64([0x16, sha1(FileContent)])。也就是，文件的内容的sha1值（20个字节），前面加一个byte（值为0x16），构成 21 字节的二进制数据，然后对这 21 字节的数据做 urlsafe 的 base64 编码。
 * 如果文件 > 4M，则 hash = UrlsafeBase64([0x96, sha1([sha1(Block1), sha1(Block2), ...])])，其中 Block 是把文件内容切分为 4M 为单位的一个个块，也就是 BlockI = FileContent[I*4M:(I+1)*4M]。
 * 为何需要公开 hash/etag 算法？这个和 “消重” 问题有关，详细见：
 *
 * https://developer.qiniu.com/kodo/kb/1365/how-to-avoid-the-users-to-upload-files-with-the-same-key
 * http://segmentfault.com/q/1010000000315810
 * 为何在 sha1 值前面加一个byte的标记位(0x16或0x96）？
 *
 * 0x16 = 22，而 2^22 = 4M。所以前面的 0x16 其实是文件按 4M 分块的意思。
 * 0x96 = 0x80 | 0x16。其中的 0x80 表示这个文件是大文件（有多个分块），hash 值也经过了2重的 sha1 计算。
 * 1G => 2562B == 2.501953125KB
 * @author 陈翔
 */
public class QETag {
    public static final int CHUNK_SIZE = 1 << 22;

    public static byte[] sha1(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("sha1");
        return mDigest.digest(data);
    }

    public static String urlSafeBase64Encode(byte[] data) {
        String encodedString = DatatypeConverter.printBase64Binary(data);
        encodedString = encodedString.replace('+', '-').replace('/', '_');
        return encodedString;
    }

    public static String calcETag(String fileName) throws IOException,
            NoSuchAlgorithmException {
        String etag = "";
        File file = new File(fileName);
        if (!(file.exists() && file.isFile() && file.canRead())) {
            System.err.println("Error: File not found or not readable");
            return etag;
        }
        long fileLength = file.length();
        FileInputStream inputStream = new FileInputStream(file);
        if (fileLength <= CHUNK_SIZE) {
            byte[] fileData = new byte[(int) fileLength];
            inputStream.read(fileData, 0, (int) fileLength);
            byte[] sha1Data = sha1(fileData);
            int sha1DataLen = sha1Data.length;
            byte[] hashData = new byte[sha1DataLen + 1];
            System.arraycopy(sha1Data, 0, hashData, 1, sha1DataLen);
            hashData[0] = 0x16;
            etag = urlSafeBase64Encode(hashData);
        } else {
            int chunkCount = (int) (fileLength / CHUNK_SIZE);

            if (fileLength % CHUNK_SIZE != 0) {
                chunkCount += 1;
            }
            System.out.println("chunkCount:"+chunkCount);
            byte[] allSha1Data = new byte[0];
            for (int i = 0; i < chunkCount; i++) {
                byte[] chunkData = new byte[CHUNK_SIZE];
                int bytesReadLen = inputStream.read(chunkData, 0, CHUNK_SIZE);
                byte[] bytesRead = new byte[bytesReadLen];
                //读入新数组=>bytesRead
                System.arraycopy(chunkData, 0, bytesRead, 0, bytesReadLen);
                //得到sha1
                byte[] chunkDataSha1 = sha1(bytesRead);
                byte[] newAllSha1Data = new byte[chunkDataSha1.length
                        + allSha1Data.length];
                System.arraycopy(allSha1Data, 0, newAllSha1Data, 0,
                        allSha1Data.length);
                System.arraycopy(chunkDataSha1, 0, newAllSha1Data,
                        allSha1Data.length, chunkDataSha1.length);
                allSha1Data = newAllSha1Data;
                System.out.println(allSha1Data.length);
            }
            byte[] allSha1DataSha1 = sha1(allSha1Data);
            byte[] hashData = new byte[allSha1DataSha1.length + 1];
            System.arraycopy(allSha1DataSha1, 0, hashData, 1,
                    allSha1DataSha1.length);
            hashData[0] = (byte) 0x96;
            etag = urlSafeBase64Encode(hashData);
        }
        inputStream.close();
        return etag;
    }

    /**
     * 拼接该分块的sha1到总的字节数组
     * @param allSha1Data
     * @param bytes
     * @return
     * @throws Exception
     */
    public static String getAllSha1(byte[] allSha1Data,byte[] bytes) throws Exception{
        //得到sha1
        byte[] chunkDataSha1 = sha1(bytes);
        //创建新数组
        byte[] newAllSha1Data = new byte[chunkDataSha1.length
                + allSha1Data.length];
        //赋值allSha1Data到新数组
        System.arraycopy(allSha1Data, 0, newAllSha1Data, 0,
                allSha1Data.length);
        //赋值该分块的sha1到新数组
        System.arraycopy(chunkDataSha1, 0, newAllSha1Data,
                allSha1Data.length, chunkDataSha1.length);
        allSha1Data = newAllSha1Data;
        return new String(allSha1Data);
    }

    /**
     * 获取最终的QETag
     * @param allSha1Data
     * @return
     * @throws Exception
     */
    public static String getFinalSha1(byte[] allSha1Data) throws Exception{
        if(allSha1Data.length<=1){
            return QETag.getETag(allSha1Data);
        }else{
            byte[] allSha1DataSha1 = sha1(allSha1Data);
            byte[] hashData = new byte[allSha1DataSha1.length + 1];
            System.arraycopy(allSha1DataSha1, 0, hashData, 1,
                    allSha1DataSha1.length);
            hashData[0] = (byte) 0x96;
            return urlSafeBase64Encode(hashData);
        }

    }

    /**
     * 小于4mb直接获取
     * @param bytes
     * @return
     */
    public static String getETag(byte[] bytes){
        if (bytes.length <= CHUNK_SIZE) {
            try {
                byte[] sha1Data = sha1(bytes);
                int sha1DataLen = sha1Data.length;
                byte[] hashData = new byte[sha1DataLen + 1];
                System.arraycopy(sha1Data, 0, hashData, 1, sha1DataLen);
                hashData[0] = 0x16;
                return urlSafeBase64Encode(hashData);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    public static int getChunks(long size){
        return Math.toIntExact(size/CHUNK_SIZE) + (size%CHUNK_SIZE==0?0:1);
    }

    public static void main(String[] args) {
        try {
//            System.out.println(calcETag("D:\\OSS\\01\\position\\TMP_BLOCK&846aee3c_4196_4721_ab30_b52d153b6a82"));
            System.out.println(calcETag("D:\\OSS\\BWhosOpposite.exe"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}