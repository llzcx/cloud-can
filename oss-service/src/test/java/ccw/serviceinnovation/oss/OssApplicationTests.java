package ccw.serviceinnovation.oss;

import ccw.serviceinnovation.oss.common.util.RedisUtil;
import ccw.serviceinnovation.oss.manager.nacos.Host;
import ccw.serviceinnovation.oss.manager.nacos.TrackerService;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.annotation.DubboReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import service.StorageTempObjectService;

import java.util.List;
import java.util.Random;

@SpringBootTest
class OssApplicationTests {

    @Test
    void initApiResource() {

    }

    @Autowired
    public RedisUtil redisUtil;

    @Autowired
    TrackerService trackerService;












}
