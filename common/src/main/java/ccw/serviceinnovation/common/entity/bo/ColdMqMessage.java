package ccw.serviceinnovation.common.entity.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class ColdMqMessage implements Serializable {
    private Long objectId;
    private String etag;


    public ColdMqMessage(Long objectId, String etag) {
        this.objectId = objectId;
        this.etag = etag;
    }
}
