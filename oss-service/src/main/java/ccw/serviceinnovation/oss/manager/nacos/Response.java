package ccw.serviceinnovation.oss.manager.nacos;


import lombok.Data;

import java.util.List;

@Data
public class Response {
    private String groupName;
    private List<Host> hosts;
}