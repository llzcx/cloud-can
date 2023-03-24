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
     * 正常数据去重
     */
    String NOR_GROUP = "NOR_GROUP:";

    /**
     *
     */
    String NOR_COUNT = "NOR_COUNT:";


    /**
     * 归档数据去重
     */
    String COLD_NAME = "COLD_NAME:";

    /**
     * 归档数据去重
     */
    String COLD_COUNT = "COLD:";

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

    /**
     * 文件归档事件 etag=>hash
     */
    String FREEZE = "FREEZE:";

    /**
     * 对象目前的状态
     */
    String OBJECT_STATE = "OBJECT_STATE:";

}
