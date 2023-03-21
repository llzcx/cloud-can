package ccw.serviceinnovation.common.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class LocationVo implements Serializable {
    private String ip;
    private Integer port;
    private String path;
    private String group;
    private String token;

    public LocationVo(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }
}
