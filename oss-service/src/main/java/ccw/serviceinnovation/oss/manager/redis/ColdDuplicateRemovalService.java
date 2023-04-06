package ccw.serviceinnovation.oss.manager.redis;

import ccw.serviceinnovation.oss.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static ccw.serviceinnovation.common.constant.RedisConstant.*;

/**
 * @author 陈翔
 */
@Component
@Slf4j
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

    public boolean save(String etag,String name) {
        log.info("cold save:{}/{}",name,etag);
        redisUtil.hset(NAME_PREFIX, etag, name);
        redisUtil.hincr(COUNT_PREFIX, etag, 1);
        return true;
    }

    public long del(String etag) {
        log.info("cold del:{}",etag);
        long hincr = redisUtil.hincr(COUNT_PREFIX, etag, -1);
        if(hincr<=0){
            redisUtil.hdel(COUNT_PREFIX, etag);
            redisUtil.hdel(NAME_PREFIX, etag);
        }
        return hincr;
    }
}
