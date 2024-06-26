package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.oss.manager.redis.ChunkRedisService;
import ccw.serviceinnovation.oss.pojo.vo.FragmentVo;
import ccw.serviceinnovation.oss.service.IFragmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 陈翔
 */
@Service
public class FragmentServiceImpl  implements IFragmentService{

    @Autowired
    ChunkRedisService chunkRedisService;

    @Override
    public List<FragmentVo> listFragments(String bucketName) {
        return chunkRedisService.listsChunkBo(bucketName);
    }

    @Override
    public Boolean deleteFragment(String bucketName, String blockToken) {
        return chunkRedisService.removeChunk(bucketName, blockToken);
    }


}
