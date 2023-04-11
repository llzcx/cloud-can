package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.Api;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 陈翔
 */
@Mapper
public interface ApiMapper  extends BaseMapper<Api> {
    /**
     *初始化接口
     * @param name
     * @param description
     * @return
     */
    void init(@Param("name") String name,@Param("description") String description,@Param("type") String type,@Param("target")String target);

}
