package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.constant.ACLEnum;
import ccw.serviceinnovation.common.entity.ObjectTagObject;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.ManageObjectMapper;
import ccw.serviceinnovation.oss.mapper.ObjectTagMapper;
import ccw.serviceinnovation.oss.mapper.ObjectTagObjectMapper;
import ccw.serviceinnovation.oss.pojo.vo.*;
import ccw.serviceinnovation.oss.service.IManageObjectService;
import ccw.serviceinnovation.oss.service.IObjectService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 杨世博
 */
@Service
@Slf4j
public class ManageObjectServiceImpl extends ServiceImpl<ManageObjectMapper, OssObject> implements IManageObjectService {

    @Autowired
    private ManageObjectMapper manageObjectMapper;

    @Autowired
    private BucketMapper bucketMapper;

    @Autowired
    private ObjectTagObjectMapper objectTagObjectMapper;

    @Autowired
    private ObjectTagMapper objectTagMapper;

    @Autowired
    private IObjectService objectService;

    @Override
    public Boolean deleteObject(List<Long> objectIdList) {

        int delete = 0;
        for (Long aLong : objectIdList) {
            delete += manageObjectMapper.delete(MPUtil.queryWrapperEq("id",aLong));
            List<ObjectTagObject> objectTagObjectList = objectTagObjectMapper.selectList(MPUtil.queryWrapperEq("object_id", aLong));
            for (ObjectTagObject objectTagObject : objectTagObjectList) {
                delete += objectTagMapper.delete(MPUtil.queryWrapperEq("id",objectTagObject.getTagId()));
            }
            delete += objectTagObjectMapper.deleteTagByObjectId(aLong);
        }
        return delete > 0;
    }

    @Override
    public RPage<ManageObjectListVo> getObjectList(String keyword, Integer pageNum, Integer size) {
        Long longKeyword = -1L;

        RPage<ManageObjectListVo> objectRPage;

        List<OssObject> ossObjects = new LinkedList<>();
        try {
            longKeyword = Long.valueOf(keyword);
        } catch (Exception e){
            ossObjects = manageObjectMapper.selectObjectListByString(keyword, (pageNum - 1) * size, size);
            List<ManageObjectListVo> objectListVos = new LinkedList<>();
            for (OssObject ossObject : ossObjects) {
                ManageObjectListVo objectListVo = new ManageObjectListVo();
                BeanUtil.copyProperties(ossObject,objectListVo);
                objectListVos.add(objectListVo);
            }
            objectRPage = new RPage<>(pageNum,size,objectListVos);
            objectRPage.setTotalCountAndTotalPage(
                    manageObjectMapper.selectObjectCountBucketName(keyword)
            );
            return objectRPage;
        }

        ossObjects = manageObjectMapper.selectObjectList(keyword, longKeyword, (pageNum - 1) * size, size);
        List<ManageObjectListVo> objectListVos = new LinkedList<>();
        for (OssObject ossObject : ossObjects) {
            ManageObjectListVo objectListVo = new ManageObjectListVo();
            BeanUtil.copyProperties(ossObject,objectListVo);
            objectListVos.add(objectListVo);
        }
        objectRPage = new RPage<>(pageNum,size,objectListVos);
        objectRPage.setTotalCountAndTotalPage(manageObjectMapper.selectObjectCount(keyword, longKeyword));
        return objectRPage;
    }

    @Override
    public ManageObjectDetailedVo getObject(Long id) {
        OssObject ossObject = manageObjectMapper.selectById(id);

        if (ossObject == null){
            return null;
        }

        ManageObjectDetailedVo objectVo = new ManageObjectDetailedVo();
        BeanUtil.copyProperties(ossObject,objectVo);
        objectVo.setObjectAclString(ACLEnum.getEnum(ossObject.getObjectAcl()).getMessage());

        String bucketName = manageObjectMapper.getBucketName(id);
        String objectName = manageObjectMapper.getObjectName(id);
        ObjectStateVo state = objectService.getState(bucketName, objectName);

        objectVo.setState(state.getStateStr());
        return objectVo;
    }

    @Override
    public RPage<ManageObjectListVo> getSubObjects(String keyword, String parent, Integer pageNum, Integer size) {

        Long parentLong = null;

        try {
            parentLong = Long.valueOf(parent);
        }catch (Exception e){
            parentLong = null;
        } finally {
            RPage<ManageObjectListVo> objectRPage;

            List<OssObject> ossObjects = manageObjectMapper.selectObjectListWithParent(keyword, parentLong, (pageNum - 1) * size, size);
            List<ManageObjectListVo> objectListVos = new LinkedList<>();
            for (OssObject ossObject : ossObjects) {
                ManageObjectListVo objectListVo = new ManageObjectListVo();
                BeanUtil.copyProperties(ossObject,objectListVo);
                objectListVos.add(objectListVo);
            }
            objectRPage = new RPage<>(pageNum,size,objectListVos);
            objectRPage.setTotalCountAndTotalPage(manageObjectMapper.selectObjectCountWithParent(keyword, parentLong));
            return objectRPage;
        }

    }
}
