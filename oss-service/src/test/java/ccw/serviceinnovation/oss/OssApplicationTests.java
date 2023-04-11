package ccw.serviceinnovation.oss;

import ccw.serviceinnovation.common.constant.AuthorityConstant;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.oss.common.util.RedisUtil;
import ccw.serviceinnovation.oss.manager.authority.api.ApiService;
import ccw.serviceinnovation.oss.manager.authority.bucketacl.BucketAclService;
import ccw.serviceinnovation.oss.manager.authority.bucketpolicy.BucketPolicyService;
import ccw.serviceinnovation.oss.manager.authority.objectacl.ObjectAclService;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.UserMapper;
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





}
