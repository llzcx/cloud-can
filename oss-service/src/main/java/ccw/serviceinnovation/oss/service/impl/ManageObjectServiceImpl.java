package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.ManageObjectMapper;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageObjectService;
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
public class ManageObjectServiceImpl extends ServiceImpl<ManageObjectMapper, OssObject> implements IManageObjectService {

    @Autowired
    private ManageObjectMapper manageObjectMapper;

    @Autowired
    private BucketMapper bucketMapper;

    @Override
    public Boolean deleteObject(Long id) {
        int delete = manageObjectMapper.delete(MPUtil.queryWrapperEq("id", id));
        return delete > 0;
    }

    @Override
    public RPage<OssObject> getObjectList(Long userId, Long bucketId, String bucketName, Integer pageNum, Integer size) {
        List<OssObject> ossObjectList;
        RPage<OssObject> ossObjectRPage;

        ossObjectList = manageObjectMapper.selectObjectList(
                bucketId, bucketMapper.selectBucketIdByName(bucketName), (pageNum - 1) * size, size);

        ossObjectRPage = new RPage<>(pageNum, size, ossObjectList);

        ossObjectRPage.setTotalCountAndTotalPage(
                manageObjectMapper.selectObjectCount(bucketId, bucketMapper.selectBucketIdByName(bucketName)));

        return ossObjectRPage;
    }
}
