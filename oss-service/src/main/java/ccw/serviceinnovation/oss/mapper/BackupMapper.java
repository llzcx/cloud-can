package ccw.serviceinnovation.oss.mapper;

import ccw.serviceinnovation.common.entity.Backup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

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
    List<Backup> selectBackup(@Param("bucketName") String bucketName, @Param("objectName")String objectName);

    /**
     * 通过目标对象还原源对象
     * @param bucketName
     * @param objectName
     * @return
     */
    Backup selectBackupByTarget(@Param("bucketName") String bucketName, @Param("objectName")String objectName);
}
