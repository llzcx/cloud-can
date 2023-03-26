package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.Backup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 陈翔
 */
@Mapper
public interface BackupMapper extends BaseMapper<Backup> {
    /**
     * 查询这个对象的备份列表
     * @param objectName
     * @return
     */
    Backup selectBackup(String bucketName,String objectName);
}
