package ccw.serviceinnovation.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author 陈翔
 * @since 2023-01-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OssObject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 对象名字[全称]
     */
    private String name;

    /**
     * 桶ID
     */
    private Long bucketId;

    /**
     * 文件扩展名
     */
    private Integer ext;

    /**
     * 文件的MD5值
     */
    private String etag;

    /**
     * 文件总大小
     */
    private Long size;

    /**
     * 是否为文件夹
     */
    private Boolean isFolder;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 最近更新时间
     */
    private String lastUpdateTime;

    /**
     * 对象访问控制
     */
    private Integer objectAcl;

    /**
     * 父级对象id
     */
    private Long parent;

    /**
     * 加密方式
     */
    private Integer secret;

    /**
     * 存储水平
     */
    private Integer storageLevel;

    /**
     * 是否为备份文件
     */
    private Boolean isBackup;


    public OssObject(){

    }

    public OssObject(String objectName, Long bucketId, String etag, Integer ext, String createTime, String lastUpdateTime) {
        this.name = objectName;
        this.bucketId = bucketId;
        this.etag = etag;
        this.ext = ext;
        this.createTime = createTime;
        this.lastUpdateTime = lastUpdateTime;
    }
}
