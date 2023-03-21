package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.oss.pojo.vo.AllBucketMessageVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author 杨世博
 * 获取用户对象存储可展示的数据
 */
public interface IAllBucketMessageService extends IService<AllBucketMessageVo> {

    /**
     * 获取Bucket可展示的数据
     * @return
     */
    AllBucketMessageVo getMessage();
}
