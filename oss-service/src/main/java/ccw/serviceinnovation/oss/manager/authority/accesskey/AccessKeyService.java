package ccw.serviceinnovation.oss.manager.authority.accesskey;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.pojo.dto.MessageDto;
import ccw.serviceinnovation.oss.service.IAccessKeyService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 处理AccessKey权限业务
 * @author 陈翔
 */
@Component
public class AccessKeyService {

    @Autowired
    IAccessKeyService accessKeyService;

    Boolean handle(OssObject ossObject, String accessKey){
        Map<String, MessageDto> accessKeys = accessKeyService.getAccessKeys(ossObject.getId());
        return false;
    }
}
