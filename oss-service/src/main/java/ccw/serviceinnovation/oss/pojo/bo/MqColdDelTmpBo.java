package ccw.serviceinnovation.oss.pojo.bo;

import lombok.Data;

/**
 * @author 陈翔
 */
@Data
public class MqColdDelTmpBo {
    private String ip;
    private Integer port;
    private String token;

    public MqColdDelTmpBo() {
    }

    public MqColdDelTmpBo(String ip, Integer port, String token) {
        this.ip = ip;
        this.port = port;
        this.token = token;
    }
}
