package ccw.serviceinnovation.common.nacos;


import lombok.Data;

import java.util.List;

/**
 * @author 陈翔
 */
@Data
public class Response {
    private String groupName;
    private List<Host> hosts;
}