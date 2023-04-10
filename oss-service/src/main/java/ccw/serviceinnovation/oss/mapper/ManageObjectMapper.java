package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.OssObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 杨世博
 */
@Mapper
public interface ManageObjectMapper extends BaseMapper<OssObject> {
    /**
     * 根据bucketName筛选分页查找Object
     * @param keyword
     * @param offset
     * @param size
     * @return
     */
    List<OssObject> selectObjectListByString(String keyword, Integer offset, Integer size);

    /**
     * 根据bucketId，bucketName筛选分页后的页数
     * @param keyword
     * @param longKeyword
     * @return
     */
    Integer selectObjectCount(String keyword, Long longKeyword);

    /**
     * 根据 name 获取数量
     * @param keyword
     * @return
     */
    Integer selectObjectCountBucketName(String keyword);

    /**
     * 通过用户Id，bucketId，bucketName搜索Object列表
     * @param keyword
     * @param longKeyword
     * @param offset
     * @param size
     * @return
     */
    List<OssObject> selectObjectList(String keyword, Long longKeyword, Integer offset, Integer size);

    /**
     * 通过ObjectId获取BucketName
     * @param objectId
     * @return
     */
    String getBucketName(Long objectId);

    /**
     * 通过ObjectId获取ObjectName
     * @param objectId
     * @return
     */
    String getObjectName(Long objectId);

    /**
     * 通过文件前缀名，搜索parent文件夹下的对象
     * @param keyword
     * @param parent
     * @param offset
     * @param size
     * @return
     */
    List<OssObject> selectObjectListWithParent(String keyword, Long parent, Integer offset, Integer size);

    /**
     * 通过文件前缀名获取parent文件夹下的对象数量
     * @param keyword
     * @param parent
     * @return
     */
    Integer selectObjectCountWithParent(String keyword, Long parent);
}
