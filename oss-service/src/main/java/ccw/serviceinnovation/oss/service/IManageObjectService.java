package ccw.serviceinnovation.oss.service;


import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 杨世博
 */
public interface IManageObjectService extends IService<OssObject> {

    /**
     * 删除Object及其相关信息
     * 1-标签
     * @param id
     * @return
     */
    Boolean deleteObject(Long id);

    /**
     * 获取Object列表
     * 1-根据用户Id筛选
     * 2-根据bucketId，bucketName筛选
     * @param userId
     * @param bucketId
     * @param bucketName
     * @param pageNum
     * @param size
     * @return
     */
    RPage<OssObject> getObjectList(Long userId, Long bucketId, String bucketName, Integer pageNum, Integer size);
}
