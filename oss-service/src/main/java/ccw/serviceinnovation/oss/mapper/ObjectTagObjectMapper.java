package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.ObjectTagObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 杨世博
 */
@Mapper
public interface ObjectTagObjectMapper extends BaseMapper<ObjectTagObject> {

    /**
     * 删除对象标签
     * @param objectId
     * @return
     */
    Long deleteTagByObjectId(Long objectId);
}
