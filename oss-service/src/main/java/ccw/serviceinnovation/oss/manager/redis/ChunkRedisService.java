package ccw.serviceinnovation.oss.manager.redis;

import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.oss.common.util.RedisUtil;
import ccw.serviceinnovation.oss.pojo.bo.ChunkBo;
import ccw.serviceinnovation.oss.pojo.vo.FragmentVo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ccw.serviceinnovation.common.constant.RedisConstant.*;
import static ccw.serviceinnovation.oss.constant.ObjectConstant.CHUNK_SIZE;


/**
 * @author 陈翔
 */
@Component
public class ChunkRedisService {
    /**
     * 某个token当前的sha1值和分块信息
     */
    private final String CHUNK_SHA1_PREFIX = OSS + OBJECT_CHUNK + CHUNK_SHA1;

    /**
     * 某个token对应的分块上传事件的信息
     */
    private final String BLOCK_TOKEN_PREFIX = OSS + OBJECT_CHUNK + BUCKET_NAME;


    private final String CHUNK_BIT_PREFIX = OSS + OBJECT_CHUNK + CHUNK_BIT;

    private final String CHUNK_CRC_PREFIX = OSS + OBJECT_CHUNK + CHUNK_CRC;

    @Autowired
    RedisUtil redisUtil;


    public void saveChunkBit(String blockToken, Integer chunk) {
        redisUtil.setBit(CHUNK_BIT_PREFIX + blockToken, chunk, true);
    }


    public void saveCheckSum(String eventId, String value) {
        redisUtil.set(CHUNK_CRC_PREFIX + eventId, value);
    }

    public String getCheckSum(String blockToken) {
        return redisUtil.get(CHUNK_CRC_PREFIX + blockToken);
    }



    public Boolean removeChunk(String bucketName, String blockToken) {
        redisUtil.hdel(BLOCK_TOKEN_PREFIX + bucketName, blockToken);
        redisUtil.del(CHUNK_BIT_PREFIX + blockToken);
        return true;
    }


    /**
     * 判断文件是否有分块已上传
     *
     * @param blockToken
     * @return
     */
    private boolean isExist(String bucketName, String blockToken) {
        return redisUtil.hExists(BLOCK_TOKEN_PREFIX + bucketName, blockToken);
    }


    public ChunkBo saveBlockToken(String bucketName, String eventId, String etag, Long userId, Long bucketId, Long size, Long parentObjectId,
                                  Integer objectAcl, String name, String groupId) {
        ChunkBo chunkBo = new ChunkBo(etag, userId, bucketId, size, parentObjectId, objectAcl, name, groupId);
        redisUtil.hset(BLOCK_TOKEN_PREFIX + bucketName, eventId, JSONObject.toJSONString(chunkBo));
        return chunkBo;
    }

    public ChunkBo getChunkBo(String bucketName, String blockToken) {
        return JSONObject.parseObject(redisUtil.hget(BLOCK_TOKEN_PREFIX + bucketName, blockToken), ChunkBo.class);
    }


    /**
     * 计数
     * @param bucketName
     * @param blockToken
     * @return
     */
    public int counter(String bucketName, String blockToken) {
        if (isExist(bucketName, blockToken)) {
            int count = 0;
            ChunkBo chunkBo = JSONObject.parseObject(redisUtil.hget(BLOCK_TOKEN_PREFIX + bucketName, blockToken), ChunkBo.class);
            Long size = chunkBo.getSize();
            int chunks = (int) Math.ceil((double) size / CHUNK_SIZE);
            for (int i = 0; i < chunks; i++) {
                if (redisUtil.getBit(CHUNK_BIT_PREFIX + blockToken, i)) {
                    count += 1;
                }
            }
            return count;
        }
        throw new OssException(ResultCode.EVENT_NULL);
    }


    /**
     * 获取ChunkBo的列表
     *
     * @param bucketName
     * @return
     */
    public List<FragmentVo> listsChunkBo(String bucketName) {
        List<FragmentVo> list = new ArrayList<>();
        System.out.println("key:" + BLOCK_TOKEN_PREFIX + bucketName);
        Map<Object, Object> hmget = redisUtil.hmget(BLOCK_TOKEN_PREFIX + bucketName);
        System.out.println("大小:" + hmget.entrySet().size());
        for (Map.Entry<Object, Object> objectObjectEntry : hmget.entrySet()) {
            String blockToken = (String) objectObjectEntry.getKey();
            ChunkBo value = JSONObject.parseObject((String) objectObjectEntry.getValue(), ChunkBo.class);
            Long size = value.getSize();
            FragmentVo fragmentVo = new FragmentVo();
            fragmentVo.setObjectName(value.getName());
            int chunks = (int) Math.ceil((double) size / CHUNK_SIZE);
            fragmentVo.setChunkNum(counter(bucketName, blockToken));
            fragmentVo.setChunks(chunks);
            BeanUtils.copyProperties(value, fragmentVo);
            fragmentVo.setBlockToken(blockToken);
            list.add(fragmentVo);
        }
        return list;
    }

}
