package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.oss.common.util.RedisUtil;
import ccw.serviceinnovation.oss.pojo.dto.AccessKeyDto;
import ccw.serviceinnovation.oss.pojo.dto.MessageDto;
import ccw.serviceinnovation.oss.service.IAccessKeyService;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * @author Joy Yang
 */
@Service
public class AccessKeyServiceImpl implements IAccessKeyService {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public Map<String, MessageDto> createAccessKey(Long objectId, Long survivalTime) {

        String accessKeyId = objectId.toString();
        String accessKeySecret = UUID.randomUUID().toString();
        MessageDto messageDto = new MessageDto();
        messageDto.setSurvivalTime(survivalTime);
        messageDto.setCreationTime(DateUtil.now());

        redisUtil.hset(accessKeyId,accessKeySecret, JSON.toJSONString(messageDto));

        //获取所有AccessKey
        Map<String, MessageDto> allAccessKey = (Map<String, MessageDto>) (Object) redisUtil.hgetall(accessKeyId);

        return allAccessKey;
    }

    @Override
    public Map<String, MessageDto> getAccessKeys(Long objectId) {
        //获取所有AccessKey
        Map<String, MessageDto> allAccessKey = (Map<String, MessageDto>) (Object) redisUtil.hgetall(String.valueOf(objectId));

        return allAccessKey;
    }

    @Override
    public Map<String, MessageDto> deleteAccessKey(AccessKeyDto accessKeyDto) {

        redisUtil.hdel(String.valueOf(accessKeyDto.getAccessKeyId()), accessKeyDto.getAccessKeySecret());

        Map<String, MessageDto> allAccessKey = (Map<String, MessageDto>) (Object) redisUtil.hgetall(String.valueOf(accessKeyDto.getAccessKeyId()));

        return allAccessKey;
    }
}
