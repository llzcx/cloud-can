package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.ObjectTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author 杨世博
 */
@Mapper
@Repository
public interface ObjectTagMapper extends BaseMapper<ObjectTag> {

    /**
     * 获取对象标签
     * @param objectId
     * @return
     */
    List<ObjectTag> getObjectTag(Long objectId);

    /**
     * 插入对象
     * @param objectTag
     * @return
     */
    Long insertTag(ObjectTag objectTag);
}
