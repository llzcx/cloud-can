package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.Bucket;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 杨世博
 */
@Mapper
public interface ManageBucketMapper extends BaseMapper<Bucket> {

    /**
     * 分页查找Bucket
     * @param offset
     * @param size
     * @return
     */
    List<Bucket> getBucketList(Integer offset, Integer size);

    /**
     * 查找Bucket的数量
     * @return
     */
    Integer selectAllCount();

    /**
     * 根据userId分页查找Bucket
     * @param offset
     * @param size
     * @param keyword
     * @param longKeyword
     * @return
     */
    List<Bucket> getBucketListByKeyword(Integer offset, Integer size, String keyword, Long longKeyword);
}
