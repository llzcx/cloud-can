package ccw.serviceinnovation.oss.manager.redis;

import ccw.serviceinnovation.oss.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static ccw.serviceinnovation.common.constant.RedisConstant.*;

/**
 * @author 陈翔
 */
@Component
public class NorDuplicateRemovalService {

    @Autowired
    RedisUtil redisUtil;

    private String COUNT_PREFIX = OSS + DUPLICATE_REMOVAL + NOR_COUNT;
    private String GROUP_PREFIX = OSS + DUPLICATE_REMOVAL + NOR_GROUP;
    public long getCount(String etag) {
        return Long.parseLong(redisUtil.hget(COUNT_PREFIX, etag));
    }

    public String getGroup(String etag) {
        return redisUtil.hget(GROUP_PREFIX, etag);
    }

    public boolean save(String etag,String group) {
        redisUtil.hset(GROUP_PREFIX, etag, group);
        redisUtil.hincr(COUNT_PREFIX, etag, 1);
        return true;
    }

    public long del(String etag) {
        long hincr = redisUtil.hincr(COUNT_PREFIX, etag, -1);
        if(hincr==0){
            redisUtil.hdel(GROUP_PREFIX, etag);
            redisUtil.hdel(COUNT_PREFIX, etag);
        }
        return hincr;
    }
}
