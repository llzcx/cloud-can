package ccw.serviceinnovation.oss.service;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.UserFavorite;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 杨世博
 */
public interface IUserFavoriteService extends IService<UserFavorite> {

    /**
     * 添加bucket收藏
     * @param bucketName
     * @param userId UserID
     * @return 是否添加成功
     * @throws Exception
     */
    List<Bucket> putUserFavorite(String bucketName, Long userId) throws Exception;

    /**
     * 删除bucket收藏
     * @param id
     * @param bucketName ID
     * @return 是否删除成功
     * @throws Exception
     */
    List<Bucket> delete(Long id, String bucketName) throws Exception;

    /**
     * 获取用户收藏的桶
     * @param userId
     * @return
     */
    List<Bucket> getUserFavorite(Long userId);
}
