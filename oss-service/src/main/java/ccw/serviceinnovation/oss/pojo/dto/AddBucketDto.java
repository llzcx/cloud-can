package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户添加bucket的传输类
 * @author 陈翔
 */
@Data
public class AddBucketDto implements Serializable {
    /**
     * 桶名字
     */
    private String bucketName;

    /**
     * 桶读写权限ACL
     */
    private Integer bucketAcl;


    /**
     * 加密方式
     */
    private Integer secret;

}
