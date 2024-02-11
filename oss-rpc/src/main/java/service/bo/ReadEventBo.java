package service.bo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
public class ReadEventBo implements Serializable {
    private Long size;

    public ReadEventBo(Long size) {
        this.size = size;
    }
}
