package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class AddBucketDto implements Serializable {
    /**
     * 桶名字
     */
    private String bucketName;

    /**
     * 归档类型
     */
    private Integer storageType;

    /**
     * 桶读写权限ACL
     */
    private Integer bucketAcl;


    /**
     * 加密方式
     */
    private Integer secret;

}
