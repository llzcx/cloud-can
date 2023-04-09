package ccw.serviceinnovation.oss.pojo.vo;

import ccw.serviceinnovation.oss.pojo.bo.FileTypeBo;
import lombok.Data;

import java.util.List;

/**
 * Bucket内的所有文件类型，及其数量
 * @author 杨世博
 */
@Data
public class BucketFileTypeVo {

    /**
     * 这个bucket内的所有文件类型和数量
     */
    private List<FileTypeBo> fileTypeList;
}
