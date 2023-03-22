package ccw.serviceinnovation.oss.manager.redis;

import ccw.serviceinnovation.common.nacos.Host;
import ccw.serviceinnovation.oss.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static ccw.serviceinnovation.common.constant.RedisConstant.*;

/**
 * @author 陈翔
 */
@Component
public class DuplicateRemovalService {

    @Autowired
    RedisUtil redisUtil;

    private String DUPLICATE_REMOVAL_PREFIX = OSS + DUPLICATE_REMOVAL;

    public Boolean isExist(String etag){
        return redisUtil.exists(DUPLICATE_REMOVAL_PREFIX+OBJECT_ADDR+etag);
    }

    public Host getAddr(String etag){
        String addr = redisUtil.get(DUPLICATE_REMOVAL_PREFIX + OBJECT_ADDR + etag);
        if(addr==null){
            return null;
        }
        Host host = new Host();
        int index = addr.indexOf(":");
        String ip = addr.substring(0, index);
        Integer port = Integer.valueOf(addr.substring(index));
        host.setIp(ip);
        host.setPort(port);
        return host;
    }

    public void saveKey(String etag,String ip,Integer port){
        redisUtil.set(DUPLICATE_REMOVAL_PREFIX+OBJECT_ADDR+etag, ip+":"+port);
    }


}
