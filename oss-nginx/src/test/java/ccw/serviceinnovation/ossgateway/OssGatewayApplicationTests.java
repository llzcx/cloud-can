package ccw.serviceinnovation.ossgateway;

import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.ossgateway.mapper.OssObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class OssGatewayApplicationTests {

    @Autowired
    OssObjectMapper ossObjectMapper;

    @Test
    void contextLoads() {
          List<OssObject> list= ossObjectMapper.selectList(null);
    }

}
