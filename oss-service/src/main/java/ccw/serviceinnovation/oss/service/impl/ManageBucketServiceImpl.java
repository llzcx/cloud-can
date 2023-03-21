package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.ManageBucketMapper;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageBucketService;
import ccw.serviceinnovation.oss.service.IObjectService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 杨世博
 */
@Service
@Slf4j
public class ManageBucketServiceImpl extends ServiceImpl<ManageBucketMapper, Bucket> implements IManageBucketService {

    @Autowired
    private ManageBucketMapper manageBucketMapper;

    @Autowired
    private IObjectService objectService;

    @Override
    public RPage<Bucket> getBucketList(Long userId,Integer pageNum, Integer size) {
        List<Bucket> bucketList;
        RPage<Bucket> bucketRPage;
        if (userId==null && userId==0){
            bucketList = manageBucketMapper.getBucketList((pageNum-1)*size, size);
            bucketRPage = new RPage<>(pageNum,size,bucketList);
            bucketRPage.setTotalCountAndTotalPage(manageBucketMapper.selectAllCount());
        }else {
            bucketList = manageBucketMapper.getBucketListById((pageNum-1)*size, size, userId);
            bucketRPage = new RPage<>(pageNum,size,bucketList);
            bucketRPage.setTotalCountAndTotalPage(manageBucketMapper.selectCount(MPUtil.queryWrapperEq("userId", userId)));
        }
        return bucketRPage;
    }

    @Override
    public Boolean deleteBucket(Long userId,String name) throws Exception{
        int delete = manageBucketMapper.delete(MPUtil.queryWrapperEq("user_id",userId, "name",name));
        objectService.deleteObjects(name);
        return delete > 0;
    }
}
