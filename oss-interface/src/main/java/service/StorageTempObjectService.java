package service;

import service.bo.FilePrehandleBo;

import java.io.IOException;

/**
 * 提供缓存
 * @author 陈翔
 */
public interface StorageTempObjectService {
    /**
     * 块缓存文件转正
     * @param token
     * @param objectKey 对象键
     * @return
     * @exception Exception
     */
    Boolean blockBecomeFullMember(String token,String objectKey) throws Exception;

    /**
     * 删除缓存块文件
     * @param objectKey
     * @return
     * @exception Exception
     */
    public Boolean deleteBlockObject(String objectKey) throws Exception;

    /**
     * 写入一块缓存文件
     * @param blockToken
     * @param targetSize
     * @param bytes
     * @param srcSize
     * @param chunks
     * @param chunk
     * @param secret
     * @return
     * @throws Exception
     */
    Boolean saveBlock(String blockToken, Long targetSize, byte[] bytes, Long srcSize, Integer chunks, Integer chunk,Integer secret) throws IOException;


    /**
     * 对文件进行压缩 返回压缩的etag
     * @param etag
     * @param objectKey
     * @param press
     * @return
     */
    FilePrehandleBo preHandle(String etag,String objectKey,Boolean press,Integer secret) throws Exception;

    /**
     * 获取端口号
     * @return
     */
    String getPort();


}
