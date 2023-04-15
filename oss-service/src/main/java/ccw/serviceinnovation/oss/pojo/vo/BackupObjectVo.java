package ccw.serviceinnovation.oss.pojo.vo;

import lombok.Data;

/**
 * 备份对象列表Vo
 */
@Data
public class BackupObjectVo {
    private Long id;
    private String bucketName;
    private String objectName;
    private String  createTime;
}
