package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.OssObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author 杨世博
 */

public interface ManageObjectMapper extends BaseMapper<OssObject> {
    /**
     * 根据bucketId，bucketName筛选分页查找Object
     * @param bucketId
     * @param bucketName
     * @param offset
     * @param size
     * @return
     */
    List<OssObject> selectObjectList(Long bucketId, Long bucketName, Integer offset, Integer size);

    /**
     * 根据bucketId，bucketName筛选分页后的页数
     * @param bucketId
     * @param bucketName
     * @return
     */
    Integer selectObjectCount( Long bucketId, Long bucketName);
}
