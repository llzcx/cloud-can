package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.oss.common.util.RedisUtil;
import ccw.serviceinnovation.oss.pojo.dto.AccessKeyDto;
import ccw.serviceinnovation.oss.pojo.dto.MessageDto;
import ccw.serviceinnovation.oss.service.IAccessKeyService;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import static ccw.serviceinnovation.common.constant.RedisConstant.ACCESSKEY_ID;

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

        redisUtil.hset(ACCESSKEY_ID+accessKeyId,accessKeySecret, JSON.toJSONString(messageDto));

        //获取所有AccessKey
        Map<String, MessageDto> allAccessKey = getAccessKeys(objectId);

        return allAccessKey;
    }

    @Override
    public Map<String, MessageDto> getAccessKeys(Long objectId) {
        //获取所有AccessKey
        Map<String, MessageDto> allAccessKey = (Map<String, MessageDto>) (Object) redisUtil.hgetall(ACCESSKEY_ID + objectId);

        for (Map.Entry<String, MessageDto> entry : allAccessKey.entrySet()) {
            MessageDto messageDto = JSONObject.parseObject(String.valueOf(entry.getValue()),MessageDto.class);

            String creationTime =messageDto.getCreationTime();
            Long survivalTime = messageDto.getSurvivalTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = null;
            try {
                date = sdf.parse(creationTime);
            }catch (Exception e){
                e.printStackTrace();
            }
            Date now = new Date();
            //计算时间差
            Long nowTime = now.getTime();
            Long dateTime = date.getTime();
            if (survivalTime < (nowTime-dateTime)/1000 ){
                AccessKeyDto accessKeyDto = new AccessKeyDto();
                accessKeyDto.setAccessKeyId(objectId);
                accessKeyDto.setAccessKeySecret(entry.getKey());
                deleteAccessKey(accessKeyDto);
            }
        }

        allAccessKey = (Map<String, MessageDto>) (Object) redisUtil.hgetall(ACCESSKEY_ID + objectId);

        return allAccessKey;
    }

    @Override
    public Map<String, MessageDto> deleteAccessKey(AccessKeyDto accessKeyDto) {

        redisUtil.hdel(ACCESSKEY_ID+accessKeyDto.getAccessKeyId(), accessKeyDto.getAccessKeySecret());

        Map<String, MessageDto> allAccessKey = getAccessKeys(accessKeyDto.getAccessKeyId());

        return allAccessKey;
    }
}
