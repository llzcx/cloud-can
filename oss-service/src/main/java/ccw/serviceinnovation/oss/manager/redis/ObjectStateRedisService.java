package ccw.serviceinnovation.oss.manager.redis;

import ccw.serviceinnovation.common.constant.ObjectStateConstant;
import ccw.serviceinnovation.common.constant.StorageTypeEnum;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.RedisUtil;
import ccw.serviceinnovation.oss.mapper.OssObjectMapper;
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

    @Autowired
    OssObjectMapper ossObjectMapper;


    public boolean setState(String bucketName,String objectName,Integer state){
        redisUtil.hset(STATE_PREFIX,bucketName+"/"+objectName,state.toString());
        return true;
    }

    public boolean delState(String bucketName,String objectName){
        redisUtil.hdel(STATE_PREFIX,bucketName+"/"+objectName);
        return true;
    }
    public Integer getState(String bucketName,String objectName){
        //先查存储水平
        Integer staticState = ossObjectMapper.selectObjectStorageLevel(bucketName, objectName);
        if(staticState==null){
            throw new OssException(ResultCode.OBJECT_IS_DEFECT);
        }
        //再查是否处于解冻或者归档状态
        String trendsStateStr = redisUtil.hget(STATE_PREFIX, bucketName + "/" + objectName);
        if(trendsStateStr != null){
            Integer trendsState = Integer.valueOf(trendsStateStr);
            return trendsState;
        }else{
            if(StorageTypeEnum.STANDARD.getCode().equals(staticState)){
                return ObjectStateConstant.NOR;
            }else if (StorageTypeEnum.ARCHIVAL.getCode().equals(staticState)){
                return ObjectStateConstant.FREEZE;
            }else{
                throw new OssException(ResultCode.UNDEFINED);
            }
        }
    }
}
