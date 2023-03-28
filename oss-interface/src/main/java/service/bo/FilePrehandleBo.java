package service.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 陈翔
 */
@Data
public class FilePrehandleBo implements Serializable {
    private String etag;
    private Integer fileType;
}
