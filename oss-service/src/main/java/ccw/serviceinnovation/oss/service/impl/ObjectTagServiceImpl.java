package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.ObjectTag;
import ccw.serviceinnovation.common.entity.ObjectTagObject;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.ObjectTagMapper;
import ccw.serviceinnovation.oss.mapper.ObjectTagObjectMapper;
import ccw.serviceinnovation.oss.mapper.OssObjectMapper;
import ccw.serviceinnovation.oss.pojo.dto.TagDto;
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
     * @param objectTags
     * @return
     */
    @Override
    public List<ObjectTag> deleteObjectTag(String bucketName, String objectName, List<ObjectTag> objectTags) {
        Long objectId = ossObjectMapper.selectObjectIdByName(bucketName, objectName);

        for (ObjectTag objectTag : objectTags) {
            objectTagObjectMapper.delete(MPUtil.queryWrapperEq("object_id", objectId, "tag_id", objectTag.getId()));
            objectTagMapper.delete(MPUtil.queryWrapperEq("id", objectTag.getId()));
        }

        List<ObjectTag> objectTag = getObjectTag(bucketName, objectName);
        return objectTag;
    }

    /**
     * 添加对象标签
     * @param bucketName
     * @param objectName
     * @param objectTags
     * @return
     */
    @Override
    public List<ObjectTag> putObjectTag(String bucketName, String objectName, List<TagDto> objectTags) {
        Long objectId = ossObjectMapper.selectObjectIdByName(bucketName, objectName);

        List<ObjectTag> objectTagsOld = getObjectTag(bucketName, objectName);

        for (TagDto objectTag : objectTags) {

            //判断是否有相同的key
            int flag= 0;
            for (ObjectTag objectTagOld : objectTagsOld) {
                if (objectTagOld.getKey().equals(objectTag.getKey())){
                    flag = 1;
                }
            }

            if (flag == 1){
                continue;
            }

            ObjectTag newObjectTag = new ObjectTag();
            newObjectTag.setKey(objectTag.getKey());
            newObjectTag.setValue(objectTag.getValue());
            objectTagMapper.insertTag(newObjectTag);

            ObjectTagObject objectTagObject = new ObjectTagObject();
            objectTagObject.setObjectId(objectId);
            objectTagObject.setTagId(newObjectTag.getId());
            objectTagObjectMapper.insertTag(objectTagObject);
        }

        List<ObjectTag> newObjectTags = getObjectTag(bucketName, objectName);
        return newObjectTags;
    }
}
