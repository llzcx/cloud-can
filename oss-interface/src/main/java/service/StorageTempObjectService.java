package service;

import ccw.serviceinnovation.common.entity.LocationVo;

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
     * @return
     * @throws Exception
     */
    Boolean saveBlock(String blockToken, Long targetSize, byte[] bytes, Long srcSize, Integer chunks, Integer chunk) throws IOException;


    /**
     * 获取端口号
     * @return
     */
    String getPort();


}
