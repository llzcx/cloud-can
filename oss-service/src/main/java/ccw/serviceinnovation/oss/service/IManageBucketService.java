package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 杨世博
 */
public interface IManageBucketService extends IService<Bucket> {

    /**
     * 根据条件分页获取Bucket列表
     * @param userId 带查找用户的Id
     * @param pageNum
     * @param size
     * @return
     */
    RPage<Bucket> getBucketList(Long userId, Integer pageNum, Integer size);

    /**
     * 删除Bucket
     * @Param userId
     * @Param name
     * @return
     */
    Boolean deleteBucket(Long userId,String name) throws Exception;
}
