package ccw.serviceinnovation.oss.pojo.bo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 存在redis中键为BLOCK_TOKEN的value
 * @author 陈翔
 */
@Data
public class ChunkBo {
    /**
     * 当前已经上传到第几个分块
     */
    private Integer currentChunk;


    /**
     * 一次保留下来的etag 做校验 数据服务将用此etag为文件名 需要取到此内容去寻址
     */
    private String etag;

    /**
     * 分块任务的创建人
     */
    private Long userId;

    /**
     * 该文件属于哪个桶
     */
    private Long bucketId;

    /**
     * 文件总大小 由前端传来
     */
    private Long size;

    private String ip;

    private Integer port;

    private Long parentObjectId;

    private String name;

    private String groupId;

    public ChunkBo(Integer currentChunk, String etag, Long userId, Long bucketId, Long size, String ip,Integer port,Long parentObjectId,String name,String groupId) {
        this.currentChunk = currentChunk;
        this.etag = etag;
        this.userId = userId;
        this.bucketId = bucketId;
        this.size = size;
        this.ip = ip;
        this.port = port;
        this.parentObjectId = parentObjectId;
        this.name = name;
        this.groupId = groupId;
    }
}
