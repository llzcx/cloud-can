package ccw.serviceinnovation.oss.pojo.bo;

import ccw.serviceinnovation.common.entity.OssObject;
import lombok.Data;

import java.io.FileInputStream;

/**
 * @author 陈翔
 */
@Data
public class GetObjectBo {
    private OssObject ossObject;
    private FileInputStream inputStream;
}
