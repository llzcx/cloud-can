package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.Bucket;
import ccw.serviceinnovation.common.entity.UserFavorite;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.BucketMapper;
import ccw.serviceinnovation.oss.mapper.UserFavoriteMapper;
import ccw.serviceinnovation.oss.service.IUserFavoriteService;
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
public class UserFavoriteImpl extends ServiceImpl<UserFavoriteMapper, UserFavorite> implements IUserFavoriteService {

    @Autowired
    private UserFavoriteMapper userFavoriteMapper;

    @Autowired
    private BucketMapper bucketMapper;

    @Override
    public List<Bucket> putUserFavorite(String bucketName, Long userId) throws Exception {
        Long bucketId = bucketMapper.selectBucketIdByName(bucketName);

        if (bucketId==null) {
            return null;
        }

        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setBucketId(bucketId);
        userFavorite.setUserId(userId);

        userFavoriteMapper.putUserFavorite(userFavorite);

        List<Bucket> list = getUserFavorite(userId);

        return list;
    }

    @Override
    public List<Bucket> delete(Long id, String bucketName) throws Exception {
        Long bucketId = bucketMapper.selectBucketIdByName(bucketName);
        userFavoriteMapper.delete(MPUtil.queryWrapperEq("bucket_id", bucketId,"user_id",id));

        List<Bucket> list = getUserFavorite(id);

        return list;
    }


    @Override
    public List<Bucket> getUserFavorite(Long userId) {
        List<UserFavorite> userFavoriteList = userFavoriteMapper.selectList(MPUtil.queryWrapperEq("user_id", userId));
        List<Bucket> bucketList =  new LinkedList<>();
        for (UserFavorite userFavorite : userFavoriteList) {
            bucketList.add(bucketMapper.selectOne(MPUtil.queryWrapperEq("id",userFavorite.getBucketId())));
        }
        return bucketList;
    }


}
