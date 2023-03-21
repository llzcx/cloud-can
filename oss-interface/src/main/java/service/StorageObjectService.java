package service;

import ccw.serviceinnovation.common.entity.LocationVo;

import java.io.FileNotFoundException;
/**
 * 对象的文件操作的抽象类
 * @author 陈翔
 */
public interface StorageObjectService {


    /**
     * 将文件对象从本地删除
     * @param objectKey 对象唯一ID
     * @return 是否删除成功
     */
    Boolean deleteObject(String objectKey);

    /**
     * 获取对象数据输入流
     * @param objectKey 对象地址
     * @exception FileNotFoundException 文件未找到
     * @return 输入流
     */
    byte[] getCompleteObject(String objectKey) throws FileNotFoundException;


    Boolean save(byte[] bytes,String etag) throws Exception;


    String getExt(String objectKey);


    /**
     * 定位
     * @param etag
     * @return
     */
    LocationVo location(String etag);

}
