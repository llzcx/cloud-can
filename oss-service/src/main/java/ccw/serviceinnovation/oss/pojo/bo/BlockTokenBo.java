package ccw.serviceinnovation.oss.pojo.bo;

import lombok.Data;

/**
 * @author 陈翔
 */
@Data
public class BlockTokenBo {
    /**
     * 分块需要携带的秘钥 对应一个文件上传事件
     */
    private String blockToken;

    /**
     * 分块的服务器ip
     */
    private String ip;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 后端是否已经存在这个文件
     */
    private Boolean exist;


}
