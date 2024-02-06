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
public class NorDuplicateRemovalService {

    @Autowired
    RedisUtil redisUtil;

    private String COUNT_PREFIX = OSS + DUPLICATE_REMOVAL + NOR_COUNT;
    private String GROUP_PREFIX = OSS + DUPLICATE_REMOVAL + NOR_GROUP;

    public static void main(String[] args) {
        System.out.println(OSS + DUPLICATE_REMOVAL + NOR_GROUP);
    }
    public long getCount(String etag) {
        return Long.parseLong(redisUtil.hget(COUNT_PREFIX, etag));
    }

    public String getGroup(String key) {
        return redisUtil.hget(GROUP_PREFIX, key);
    }

    public boolean save(String key,String group) {
        log.info("nor save:{}/{}",group,key);
        redisUtil.hset(GROUP_PREFIX, key, group);
        redisUtil.hincr(COUNT_PREFIX, key, 1);
        return true;
    }

    public long del(String etag) {
        log.info("nor del:{}",etag);
        long hincr = redisUtil.hincr(COUNT_PREFIX, etag, -1);
        if(hincr<=0){
            redisUtil.hdel(GROUP_PREFIX, etag);
            redisUtil.hdel(COUNT_PREFIX, etag);
        }
        return hincr;
    }
}
