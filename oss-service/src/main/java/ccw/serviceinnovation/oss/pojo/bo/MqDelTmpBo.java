package ccw.serviceinnovation.oss.pojo.bo;

import lombok.Data;

/**
 * @author 陈翔
 */
@Data
public class MqDelTmpBo {
    private String blockToken;
    private String ip;
    private Integer port;

    public MqDelTmpBo(String blockToken, String ip, Integer port) {
        this.blockToken = blockToken;
        this.ip = ip;
        this.port = port;
    }
}
