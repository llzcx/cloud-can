package ccw.serviceinnovation.oss.manager.redis;

import ccw.serviceinnovation.oss.common.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static ccw.serviceinnovation.common.constant.RedisConstant.OBJECT_STATE;
import static ccw.serviceinnovation.common.constant.RedisConstant.OSS;

/**
 * 对象状态服务
 * @author 陈翔
 */
@Component
public class ObjectStateRedisService {
    private String STATE_PREFIX =  OSS + OBJECT_STATE;

    @Autowired
    RedisUtil redisUtil;


    public boolean setState(String bucketName,String objectName,String state){
        redisUtil.hset(STATE_PREFIX,bucketName+"/"+objectName,state);
        return true;
    }

    public boolean delState(String bucketName,String objectName){
        redisUtil.hdel(STATE_PREFIX,bucketName+"/"+objectName);
        return true;
    }
    public String getState(String bucketName,String objectName){
        return redisUtil.hget(STATE_PREFIX,bucketName+"/"+objectName);
    }
}
