package ccw.serviceinnovation.ossdata.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author 陈翔
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @Value("${server.port}")
    private String str;

    @GetMapping("/demo")
    public String test() throws Exception{
        return str;
    }


}
