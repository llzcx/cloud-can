package ccw.serviceinnovation.ossdata.manager.redis;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.util.hash.QETag;

import ccw.serviceinnovation.ossdata.bo.ChunkBo;
import ccw.serviceinnovation.ossdata.util.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static ccw.serviceinnovation.common.constant.RedisConstant.*;


/**
 * @author 陈翔
 */
@Component
public class ChunkRedisService {
    /**
     * 某个token当前的sha1值和分块信息
     */
    private final String CHUNK_SHA1_PREFIX = OSS+OBJECT_CHUNK+CHUNK_SHA1;

    /**
     * 某个token对应的分块上传事件的信息
     */
    private final String BLOCK_TOKEN_PREFIX = OSS+OBJECT_CHUNK+BUCKET_NAME;



    private final String CHUNK_BIT_PREFIX = OSS+OBJECT_CHUNK+CHUNK_BIT;

    @Autowired
    RedisUtil redisUtil;

    public Boolean saveChunkBit(String blockToken,Integer chunk){
        return redisUtil.setBit(CHUNK_BIT_PREFIX+blockToken, chunk, true);
    }




    /**
     * 获取ChunkBo
     * @param blockToken
     * @return
     */
    public ChunkBo getChunkBo(String bucketName, String blockToken) {
        return JSONObject.parseObject(redisUtil.hget(BLOCK_TOKEN_PREFIX+bucketName,blockToken), ChunkBo.class);
    }



}
