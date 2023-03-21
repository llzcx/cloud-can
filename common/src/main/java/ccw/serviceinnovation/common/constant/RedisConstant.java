package ccw.serviceinnovation.common.constant;

/**
 * Redis常量
 * @author 陈翔
 */
public interface RedisConstant {

    /**
     * 前缀
     */
    String OSS = "OSS:";


    /*--------------------------业务------------------------**/


    /**
     * 2.文件去重服务
     */
    String DUPLICATE_REMOVAL = "DUPLICATE_REMOVAL:";

    /**
     * 文件服务器 etag=>address
     */
    String OBJECT_ADDR = "OBJECT_ADDR:";

    /**
     * 1.文件分块上传业务
     */
    String OBJECT_CHUNK = "CHUNK:";
    /**
     * 记录某个文件的当前sha1 UUID=>(String)byte[20]
     */
    String CHUNK_SHA1 = "CHUNK_SHA1:";

    /**
     * 分块相关信息 UUID=>{userId,bucketId,etag}
     */
    String BLOCK_TOKEN = "BLOCK_TOKEN:";



}
