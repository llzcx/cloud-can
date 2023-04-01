package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.pojo.vo.ObjectVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 陈翔
 */
@Mapper
public interface OssObjectMapper extends BaseMapper<OssObject> {

    /**
     * 根据桶名字查找对象id
     * @param bucketName
     * @param objectName
     * @return
     */
    Long selectObjectIdByName(@Param("bucketName")String bucketName,@Param("bucketName")String objectName);

    /**
     * 根据桶名字查找对象id
     * @param bucketId
     * @param objectName
     * @return
     */
    Long selectObjectIdByIdAndName(@Param("bucketId")Long bucketId,@Param("objectName")String objectName);

    /**
     * 根据桶名字查找对象
     * @param bucketName
     * @param objectName
     * @return
     */
    OssObject selectObjectByName(@Param("bucketName")String bucketName,@Param("objectName")String objectName);



    /**
     * 有则覆盖对象 没有则添加一个对象
     * @return 如果是更新则返回2 如果是插入则返回1
     */
    Integer putObject(OssObject ossObject);

    /**
     * 有则覆盖文件夹 没有则添加一个文件夹
     * @param ossObject
     * @return 如果是更新则返回2 如果是插入则返回1
     */
    int putFolder(OssObject ossObject);

    /**
     * 分页获取对象列表
     * @param bucketName 桶名字
     * @param pageNum 第几页
     * @param size 每页数据
     * @param key 前缀匹配
     * @param parentObjectId 父级文件夹对象id
     * @return
     */
    List<ObjectVo> selectObjectList(@Param("bucketName") String bucketName,@Param("pageNum") Integer pageNum,
                                    @Param("size") Integer size, @Param("key") String key,@Param("parentObjectId")Long parentObjectId,@Param("type")Integer type);

    /**
     * 分页获取对象列表的大小
     * @param bucketName
     * @param key
     * @return
     */
    Integer selectObjectListLength(@Param("bucketName") String bucketName, @Param("key") String key);

    /**
     * 对象去重前查一下是否存过这个文件
     * @param etag
     * @return
     */
    OssObject getOssObjectByEtag(@Param("etag")String etag);

    /**
     * 查找一个对象的存储状态
     * @param bucketName
     * @param objectName
     * @return
     */
    Integer selectObjectStorageLevel(@Param("bucketName")String bucketName,@Param("objectName")String objectName);




}
