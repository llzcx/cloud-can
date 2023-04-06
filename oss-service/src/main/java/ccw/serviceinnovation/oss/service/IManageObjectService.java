package ccw.serviceinnovation.oss.service;


import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.pojo.vo.ManageObjectDetailedVo;
import ccw.serviceinnovation.oss.pojo.vo.ManageObjectListVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 杨世博
 */
public interface IManageObjectService extends IService<OssObject> {

    /**
     * 删除Object及其相关信息
     * 1-标签
     * @param objectIdList
     * @return
     */
    Boolean deleteObject(List<Long> objectIdList);

    /**
     * 获取Object列表
     * 1-根据用户Id筛选
     * 2-根据bucketId，bucketName筛选
     * @param keyword
     * @param pageNum
     * @param size
     * @return
     */
    RPage<ManageObjectListVo> getObjectList(String keyword, Integer pageNum, Integer size);

    /**
     * 获取Object的详细信息
     * @param id
     * @return
     */
    ManageObjectDetailedVo getObject(Long id);

    /**
     * 获取文件夹中的对象
     * @param keyword
     * @param parent
     * @param pageNum
     * @param size
     * @return
     */
    RPage<ManageObjectListVo> getSubObjects(String keyword, String parent, Integer pageNum, Integer size);
}
