package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.oss.mapper.AllBucketMessageVoMapper;
import ccw.serviceinnovation.oss.pojo.vo.AllBucketMessageVo;
import ccw.serviceinnovation.oss.service.IAllBucketMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 杨世博
 */
@Service
public class AllBucketMessageServiceImpl extends ServiceImpl<AllBucketMessageVoMapper,AllBucketMessageVo> implements IAllBucketMessageService {

    @Autowired
    AllBucketMessageVoMapper allBucketMessageVoMapper;

    @Override
    public AllBucketMessageVo getMessage() {
        return null;
    }
}
