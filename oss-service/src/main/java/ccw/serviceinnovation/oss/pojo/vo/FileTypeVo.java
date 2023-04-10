package ccw.serviceinnovation.oss.pojo.vo;

import lombok.Data;

/**
 * 获取bucket内的一种文件类型及其数量
 * @author 杨世博
 */
@Data
public class FileTypeVo {
    /**
     * 文件类型
     */
    private Integer ext;

    /**
     * 该文件类型在bucket内的数量
     */
    private Integer count;
}
