package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.ObjectTag;
import ccw.serviceinnovation.common.entity.ObjectTagObject;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.ObjectTagMapper;
import ccw.serviceinnovation.oss.mapper.ObjectTagObjectMapper;
import ccw.serviceinnovation.oss.mapper.OssObjectMapper;
import ccw.serviceinnovation.oss.service.IObjectTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 杨世博
 */
@Service
@Slf4j
public class ObjectTagServiceImpl extends ServiceImpl<ObjectTagMapper, ObjectTag> implements IObjectTagService {

    @Autowired
    ObjectTagMapper objectTagMapper;

    @Autowired
    OssObjectMapper ossObjectMapper;

    @Autowired
    ObjectTagObjectMapper objectTagObjectMapper;

    /**
     * 获取对象标签
     * @param bucketName
     * @param objectName
     * @return
     */
    @Override
    public List<ObjectTag> getObjectTag(String bucketName, String objectName) {
        Long objectId = ossObjectMapper.selectObjectIdByName(bucketName, objectName);
        List<ObjectTag> objectTagList = objectTagMapper.getObjectTag(objectId);
        return objectTagList;
    }

    /**
     * 删除标签
     * @param bucketName
     * @param objectName
     * @param tagId
     * @return
     */
    @Override
    public Boolean deleteObjectTag(String bucketName, String objectName, Long tagId) {
        Long objectId = ossObjectMapper.selectObjectIdByName(bucketName, objectName);
        int delete = objectTagObjectMapper.delete(MPUtil.queryWrapperEq("object_id", objectId, "tag_id", tagId));
        int deleteTag = objectTagMapper.delete(MPUtil.queryWrapperEq("tag_id", tagId));
        return delete>0 && deleteTag>0;
    }

    /**
     * 添加对象标签
     * @param bucketName
     * @param objectName
     * @param key
     * @param value
     * @return
     */
    @Override
    public List<ObjectTag> putObjectTag(String bucketName, String objectName, String key, String value) {
        Long objectId = ossObjectMapper.selectObjectIdByName(bucketName, objectName);

        ObjectTag objectTag = new ObjectTag();
        objectTag.setKey(key);
        objectTag.setValue(value);
        objectTagMapper.insertTag(objectTag);

        ObjectTagObject objectTagObject = new ObjectTagObject();
        objectTagObject.setObjectId(objectId);
        objectTagObject.setTagId(objectTag.getId());
        objectTagObjectMapper.insert(objectTagObject);

        List<ObjectTag> objectTags = getObjectTag(bucketName, objectName);
        return objectTags;
    }
}
