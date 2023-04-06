package ccw.serviceinnovation.oss.manager.redis;

import ccw.serviceinnovation.common.entity.LocationVo;
import ccw.serviceinnovation.common.exception.OssException;
import ccw.serviceinnovation.common.request.ResultCode;
import ccw.serviceinnovation.common.util.hash.QETag;
import ccw.serviceinnovation.oss.common.util.RedisUtil;
import ccw.serviceinnovation.oss.manager.consistenthashing.ConsistentHashing;
import ccw.serviceinnovation.oss.pojo.bo.ChunkBo;
import ccw.serviceinnovation.oss.pojo.vo.FragmentVo;
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



    @Autowired
    ConsistentHashing consistentHashing;


    public Boolean saveChunkBit(String blockToken,Integer chunk){
        return redisUtil.setBit(CHUNK_BIT_PREFIX+blockToken, chunk, true);
    }

    public Boolean removeChunk(String bucketName,String blockToken){
        redisUtil.hdel(BLOCK_TOKEN_PREFIX+bucketName,blockToken);
        redisUtil.del(CHUNK_BIT_PREFIX+blockToken);
        return true;
    }
    /**
     * 判断文件所有分块是否已上传
     * @param blockToken
     * @return
     */
    public boolean isUploaded(String buckName,String blockToken) {
        if (isExist(buckName,blockToken)) {
            ChunkBo chunkBo = JSONObject.parseObject(redisUtil.hget(BLOCK_TOKEN_PREFIX+buckName, blockToken), ChunkBo.class);
            Long size = chunkBo.getSize();
            int chunks = QETag.getChunks(size);
            for (int i = 0; i < chunks; i++) {
                if(!redisUtil.getBit(CHUNK_BIT_PREFIX+blockToken, i)){
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
    public byte[] getSha1(String bucketName,String blockToken) {
        if(isExist(bucketName,blockToken)){
            ChunkBo chunkBo = getChunkBo(bucketName,blockToken);
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
    private boolean isExist(String bucketName,String blockToken) {
        return redisUtil.hExists(BLOCK_TOKEN_PREFIX+bucketName,blockToken);
    }


    public ChunkBo saveBlockToken(String bucketName,String blockToken,String etag,Long userId,Long bucketId,Long size,Long parentObjectId,Integer secret,
                                  Integer objectAcl,String name){
        LocationVo location = ConsistentHashing.getStorageObjectNode(etag);
        ChunkBo chunkBo = new ChunkBo(etag,userId,bucketId,size,location.getIp(),location.getPort(),parentObjectId,secret,objectAcl,name,location.getGroup());
        redisUtil.hset(BLOCK_TOKEN_PREFIX+bucketName, blockToken,JSONObject.toJSONString(chunkBo));
        return chunkBo;
    }
    /**
     * 获取ChunkBo
     * @param blockToken
     * @return
     */
    public ChunkBo getChunkBo(String bucketName, String blockToken) {
        return JSONObject.parseObject(redisUtil.hget(BLOCK_TOKEN_PREFIX+bucketName,blockToken), ChunkBo.class);
    }

    public Integer getChunkNum(String bucketName,String blockToken){
        if (isExist(bucketName,blockToken)) {
            Integer count = 0;
            ChunkBo chunkBo = JSONObject.parseObject(redisUtil.hget(BLOCK_TOKEN_PREFIX+bucketName, blockToken), ChunkBo.class);
            Long size = chunkBo.getSize();
            int chunks = QETag.getChunks(size);
            for (int i = 0; i < chunks; i++) {
                if(redisUtil.getBit(CHUNK_BIT_PREFIX+blockToken, i)){
                    count += 1;
                }
            }
            return count;
        }
        throw new OssException(ResultCode.EVENT_NULL);
    }


    /**
     * 获取ChunkBo的列表
     * @param bucketName
     * @return
     */
    public List<FragmentVo> listsChunkBo(String bucketName) {
        List<FragmentVo> list = new ArrayList<>();
        System.out.println("key:"+BLOCK_TOKEN_PREFIX+bucketName);
        Map<Object, Object> hmget = redisUtil.hmget(BLOCK_TOKEN_PREFIX+bucketName);
        System.out.println("大小:"+hmget.entrySet().size());
        for (Map.Entry<Object, Object> objectObjectEntry : hmget.entrySet()) {
            String blockToken =(String) objectObjectEntry.getKey();
            ChunkBo value = JSONObject.parseObject((String) objectObjectEntry.getValue(), ChunkBo.class);
            FragmentVo fragmentVo = new FragmentVo();
            fragmentVo.setObjectName(value.getName());
            fragmentVo.setChunkNum(getChunkNum(bucketName, blockToken));
            fragmentVo.setChunks(QETag.getChunks(value.getSize()));
            BeanUtils.copyProperties(value, fragmentVo);
            fragmentVo.setBlockToken(blockToken);
            list.add(fragmentVo);
        }
        return list;
    }

}
