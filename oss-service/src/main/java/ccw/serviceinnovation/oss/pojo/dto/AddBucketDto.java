package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

/**
 * @author 陈翔
 */
@Data
public class AddBucketDto {
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
