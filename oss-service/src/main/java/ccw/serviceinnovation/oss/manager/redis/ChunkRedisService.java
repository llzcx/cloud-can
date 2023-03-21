package ccw.serviceinnovation.oss.manager.redis;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.util.hash.QETag;
import ccw.serviceinnovation.oss.common.util.RedisUtil;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import ccw.serviceinnovation.oss.manager.nacos.Host;
import ccw.serviceinnovation.oss.manager.nacos.TrackerService;
import ccw.serviceinnovation.oss.pojo.bo.ChunkBo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Base64;
import java.util.List;
import java.util.Random;
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
    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TrackerService trackerService;


    @Autowired
    ConsistentHashing consistentHashing;


    public Boolean saveChunk(String blockToken,Integer chunk,String sha1){
        redisUtil.hset(CHUNK_SHA1_PREFIX+blockToken, String.valueOf(chunk), sha1);
        return true;
    }

    public Boolean removeChunk(String blockToken){
        redisUtil.del(CHUNK_SHA1_PREFIX+blockToken);
        redisUtil.del(BLOCK_TOKEN_PREFIX+blockToken);
        return true;
    }
    /**
     * 判断文件所有分块是否已上传
     * @param blockToken
     * @return
     */
    public boolean isUploaded(String blockToken) {
        if (isExist(blockToken)) {
            ChunkBo chunkBo = JSONObject.parseObject(redisUtil.get(BLOCK_TOKEN_PREFIX + blockToken), ChunkBo.class);
            Long size = chunkBo.getSize();
            int chunks = QETag.getChunks(size);
            for (int i = 0; i < chunks; i++) {
                if(!redisUtil.hExists(CHUNK_SHA1_PREFIX+blockToken,String.valueOf(i))){
                    return false;
                }
            }
            return true;
        }
        return false;
    }


    /**
     * 获取完整的sha1值
     * @param blockToken
     * @return
     */
    public byte[] getSha1(String blockToken) {
        if(isExist(blockToken)){
            ChunkBo chunkBo = getObjectPosition(blockToken);
            int chunks = QETag.getChunks(chunkBo.getSize());
            int byteLen = chunks*20;
            byte[] allSha1 = new byte[byteLen];
            for (int i = 0; i < chunks ; i++) {
                String sha1 = redisUtil.hget(CHUNK_SHA1_PREFIX + blockToken, String.valueOf(i));
                byte[] decode = Base64.getDecoder().decode(sha1);
                int left = 20*i;
                System.arraycopy(decode, 0, allSha1, left, decode.length);
            }
            return allSha1;
        }
        return null;
    }

    /**
     * 判断文件是否有分块已上传
     * @param blockToken
     * @return
     */
    private boolean isExist(String blockToken) {
        return redisUtil.exists(BLOCK_TOKEN_PREFIX + blockToken);
    }


    public void saveBlockToken(String blockToken,String etag,Long userId,Long bucketId,Long size,Long parentObjectId,String name){
        LocationVo location = consistentHashing.getStorageObjectNode(etag);
        ChunkBo chunkBo = new ChunkBo(0,etag,userId,bucketId,size,location.getIp(),location.getPort(),parentObjectId,name,location.getGroup());
        redisUtil.set(BLOCK_TOKEN_PREFIX +blockToken,JSONObject.toJSONString(chunkBo));
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
