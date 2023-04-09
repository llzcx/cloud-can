package ccw.serviceinnovation.oss.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除bucket的Dto
 * @author 陈翔
 */
@Data
public class DeleteBucketDto implements Serializable {
    private Long bucketId;
}
