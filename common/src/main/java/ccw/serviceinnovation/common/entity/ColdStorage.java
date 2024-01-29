package ccw.serviceinnovation.common.entity;

import lombok.Data;

/**
 * @author 陈翔
 */
@Data
public class ColdStorage {
    private Long id;
    private String etag;
    private String coldStorageName;
    private Long count;
    public ColdStorage(String etag, String name) {
        this.etag = etag;
        this.coldStorageName = name;
    }
}
