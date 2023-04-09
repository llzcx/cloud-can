package ccw.serviceinnovation.oss.pojo.vo;

import lombok.Data;

/**
 * bucket内上传碎片Vo
 * @author 陈翔
 */
@Data
public class FragmentVo {
    private String blockToken;

    /**
     * 经前端计算完成的Etag
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

    /**
     * 上传的目标IP
     */
    private String ip;

    /**
     * 上传的目标端口号
     */
    private Integer port;

    /**
     * 文件属于哪个文件夹
     */
    private Long parentObjectId;

    /**
     * 对象名
     */
    private String objectName;

    /**
     * 加密方式
     */
    private Integer secret;


    /**
     * 权限类别
     */
    private Integer objectAcl;

    /**
     * 当前已经上传了多少分块
     */
    private Integer chunkNum;

    /**
     * 总分块数
     */
    private Integer chunks;

}
