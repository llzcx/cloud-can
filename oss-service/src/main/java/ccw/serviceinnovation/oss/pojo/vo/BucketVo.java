package ccw.serviceinnovation.oss.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author 杨世博
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BucketVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 桶ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 桶名字
     */
    private String name;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

//    /**
//     * 是否开启版本控制
//     */
//    private Boolean versionControl;

//    /**
//     * 存储等级
//     */
//    private Integer storageLevel;

    /**
     * 存储等级
     */
    private String storageLevelString;

    /**
     * 图片是否需要加水印
     */
    private Boolean watermark;

    /**
     * 是否需要加密存储
     */
    private Boolean encryption;

    /**
     * 桶读写权限ACL
     */
    private String bucketAcl;

    /**
     * Bucket 的容量，单位 B
     */
    private Long capacity;

    /**
     * Bucket 内存储的标准类型对象的容量, 单位B
     */
    private Long standardCapacity;

    /**
     * Bucket 内存储的归档类型对象的容量, 单位B
     */
    private Long pigeonholeCapacity;
}
