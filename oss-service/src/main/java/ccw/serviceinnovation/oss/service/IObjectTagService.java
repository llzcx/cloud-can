package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.ObjectTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 杨世博
 */
public interface IObjectTagService extends IService<ObjectTag> {

    /**
     * 添加对象标签
     * @param bucketName
     * @param objectName
     * @param key
     * @param value
     * @return
     */
    List<ObjectTag> putObjectTag(String bucketName, String objectName, String key, String value);

    /**
     * 获取对象标签
     * @param bucketName
     * @param objectName
     * @return
     */
    List<ObjectTag> getObjectTag(String bucketName, String objectName);

    /**
     * 删除对象标签
     * @param bucketName
     * @param objectName
     * @param tagId
     * @return
     */
    Boolean deleteObjectTag(String bucketName,String objectName,Long tagId);
}
