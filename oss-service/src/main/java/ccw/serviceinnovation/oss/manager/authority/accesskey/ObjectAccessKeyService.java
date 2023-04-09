package ccw.serviceinnovation.oss.manager.authority.accesskey;

import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.pojo.dto.MessageDto;
import ccw.serviceinnovation.oss.service.IAccessKeyService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 处理AccessKey权限业务
 * @author 陈翔
 */
@Component
public class ObjectAccessKeyService {

    @Autowired
    IAccessKeyService accessKeyService;


    /**
     * 处理AccessKey
     * @param ossObject
     * @param accessKey
     * @return
     */
    public Boolean handle(OssObject ossObject, String accessKey) {
        Map<String, MessageDto> accessKeys = accessKeyService.getAccessKeys(ossObject.getId());
        for (Map.Entry<String, MessageDto> entry : accessKeys.entrySet()) {
            String key = entry.getKey();
            MessageDto value = entry.getValue();
            if(accessKey.equals(key)){
                //说明存在这个key
                return true;
            }
        }
        return false;
    }


}
