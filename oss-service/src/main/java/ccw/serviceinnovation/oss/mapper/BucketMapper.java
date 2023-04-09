package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.Authorize;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.oss.pojo.dto.PutAuthorizeDto;
import ccw.serviceinnovation.oss.pojo.vo.AuthorizeVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 陈翔
 */
@Mapper
public interface BucketMapper extends BaseMapper<Bucket> {

    /**
     * 根据桶名字查找桶id
     * @param bucketName
     * @return
     */
    Long selectBucketIdByName(@Param("bucketName")String bucketName);



    /**
     * 根据桶名字查找桶
     * @param bucketName
     * @return
     */
    Bucket selectBucketByName(@Param("bucketName")String bucketName);


    /**
     * 根据bucketId获取bucketName
     * @param bucketId
     * @return
     */
    String getBucketName(@Param("bucketId")Long bucketId);


    /**
     * 获取bucketlist
     * @param key
     * @param offset
     * @param pagesize
     * @return
     */
    List<Bucket> selectBucketList(@Param("userId") Long userId,@Param("key") String key,@Param("offset") Integer offset,@Param("pagesize") Integer pagesize);
    Integer selectBucketListSize(@Param("userId") Long userId,@Param("key") String key);
}
