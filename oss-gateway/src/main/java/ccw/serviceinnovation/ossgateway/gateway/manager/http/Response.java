package ccw.serviceinnovation.ossgateway.gateway.manager.http;


import lombok.Data;

import java.util.List;

@Data
public class Response{
        private List<Host> hosts;
    }