package ccw.serviceinnovation.oss.manager.authority;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.entity.User;
import lombok.Data;

/**
 * 鉴权保存的信息
 */
@Data
public class AuthInfo {
    private User user;
    private Bucket bucket;
    private OssObject ossObject;
}
