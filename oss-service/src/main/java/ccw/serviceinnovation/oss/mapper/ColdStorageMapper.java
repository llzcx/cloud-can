package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.ColdStorage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 陈翔
 */
@Mapper
public interface ColdStorageMapper extends BaseMapper<ColdStorage> {
    /**
     * 通过etag获取该文件是否已经归档
     * @param etag
     * @return
     */
    ColdStorage get(String etag);
}
