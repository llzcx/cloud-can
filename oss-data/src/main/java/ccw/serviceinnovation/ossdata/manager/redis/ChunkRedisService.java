package ccw.serviceinnovation.ossdata.manager.redis;

import ccw.serviceinnovation.ossdata.bo.ChunkBo;
import ccw.serviceinnovation.ossdata.util.RedisUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private final String BLOCK_TOKEN_PREFIX = OSS+OBJECT_CHUNK+BLOCK_TOKEN;

    private final String CHUNK_BIT_PREFIX = OSS+OBJECT_CHUNK+CHUNK_BIT;
    @Autowired
    RedisUtil redisUtil;



    public Boolean saveChunk(String blockToken,Integer chunk,String sha1){
        redisUtil.hset(CHUNK_SHA1_PREFIX+blockToken, String.valueOf(chunk), sha1);
        return true;
    }

    public Boolean saveChunkBit(String blockToken,Integer chunk){
        return redisUtil.setBit(CHUNK_BIT_PREFIX+blockToken, chunk, true);
    }

    /**
     * 获取文件的地址
     * @param blockToken
     * @return
     */
    public ChunkBo getObjectPosition(String blockToken) {
        return JSONObject.parseObject(redisUtil.get(BLOCK_TOKEN_PREFIX + blockToken), ChunkBo.class);
    }

}
