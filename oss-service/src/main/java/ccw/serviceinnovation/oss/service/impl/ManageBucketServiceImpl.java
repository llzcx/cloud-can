package ccw.serviceinnovation.oss.service.impl;


import ccw.serviceinnovation.common.constant.ObjectACLEnum;
import ccw.serviceinnovation.common.constant.StorageTypeEnum;
import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.OssObject;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.ManageBucketMapper;
import ccw.serviceinnovation.oss.mapper.UserFavoriteMapper;
import ccw.serviceinnovation.oss.pojo.vo.BucketVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageBucketService;
import ccw.serviceinnovation.oss.service.IObjectService;
import ccw.serviceinnovation.oss.service.IUserFavoriteService;
import cn.hutool.core.bean.BeanUtil;
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

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private BucketMapper bucketMapper;

    @Override
    public RPage<BucketVo> getBucketList(String keyword, Integer pageNum, Integer size) {
        List<Bucket> bucketList;
        RPage<BucketVo> bucketVoRPage;

        if (keyword==null || "".equals(keyword)){
            bucketList = manageBucketMapper.getBucketList((pageNum-1)*size, size);

            List<BucketVo> bucketVos = getBucketVos(bucketList);

            bucketVoRPage = new RPage<>(pageNum,size,bucketVos);
            bucketVoRPage.setTotalCountAndTotalPage(manageBucketMapper.selectAllCount());
        }else {
            Long longKeyword = -1L;
            try {
                longKeyword = Long.valueOf(keyword);
            }catch (Exception e){
                longKeyword = -1L;
            }

            bucketList = manageBucketMapper.getBucketListByKeyword((pageNum-1)*size, size, keyword, longKeyword);

            List<BucketVo> bucketVos = getBucketVos(bucketList);

            bucketVoRPage = new RPage<>(pageNum,size,bucketVos);
            bucketVoRPage.setTotalCountAndTotalPage(manageBucketMapper.selectCount(MPUtil.queryWrapperEq("user_id", longKeyword, "name", keyword)));
        }
        return bucketVoRPage;
    }

    @Override
    public Boolean deleteBucket(Long userId,String name) throws Exception{
        Bucket bucket = bucketMapper.selectBucketByName(name);
        objectService.deleteObjects(name);
        userFavoriteMapper.delete(MPUtil.queryWrapperEq("bucket_id",bucket.getId()));
        int delete = manageBucketMapper.delete(MPUtil.queryWrapperEq("user_id",userId, "name",name));
        return delete > 0;
    }

    /**
     * 根据从数据库中搜到的bucket列表，获取bucketVo---主要为获取bucket的存储容量
     * @param buckets
     * @return
     */
    private List<BucketVo> getBucketVos(List<Bucket> buckets){

        List<BucketVo> bucketVos = new LinkedList<>();

        for (Bucket bucket : buckets) {
            Long sum = 0L;
            Long standardSum = 0L;
            Long pigeonholeSum = 0L;
            List<OssObject> ossObjects = objectService.list(MPUtil.queryWrapperEq("bucket_id",bucket.getId()));

            for (OssObject ossObject : ossObjects) {
                if (ossObject.getStorageLevel() == 1){
                    standardSum += ossObject.getSize();
                }else if (ossObject.getStorageLevel() == 3){
                    pigeonholeSum += ossObject.getSize();
                }
                sum += ossObject.getSize();
            }

            BucketVo bucketVo = new BucketVo();
            BeanUtil.copyProperties(bucket,bucketVo);

            bucketVo.setCapacity(sum);
            bucketVo.setStorageLevelString(StorageTypeEnum.getEnum(bucket.getStorageLevel()).getMessage());
            bucketVo.setBucketAcl(ObjectACLEnum.getEnum(bucket.getBucketAcl()).getMessage());
            bucketVo.setStandardCapacity(standardSum);
            bucketVo.setPigeonholeCapacity(pigeonholeSum);
            bucketVos.add(bucketVo);
        }

        return bucketVos;
    }
}
