package ccw.serviceinnovation.ossgateway.manager.redis;

import ccw.serviceinnovation.ossgateway.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static ccw.serviceinnovation.common.constant.RedisConstant.*;

/**
 * @author 陈翔
 */
@Component
public class ColdDuplicateRemovalService {

    @Autowired
    RedisUtil redisUtil;

    private String COUNT_PREFIX = OSS + DUPLICATE_REMOVAL + COLD_COUNT;
    private String NAME_PREFIX = OSS + DUPLICATE_REMOVAL + COLD_NAME;
    public Long getCount(String etag) {
        return Long.valueOf(redisUtil.hget(COUNT_PREFIX, etag));
    }

    public String getName(String etag) {
        return redisUtil.hget(NAME_PREFIX, etag);
    }

    public boolean save(String etag,String group) {
        redisUtil.hset(NAME_PREFIX, etag, group);
        redisUtil.hincr(COUNT_PREFIX, etag, 1);
        return true;
    }

    public long del(String etag) {
        long hincr = redisUtil.hincr(COUNT_PREFIX, etag, -1);
        if(hincr==0){
            redisUtil.hdel(COUNT_PREFIX, etag);
            redisUtil.hdel(NAME_PREFIX, etag);
        }
        return hincr;
    }
}
