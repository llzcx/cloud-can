package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.ManageUserMapper;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author 杨世博
 */
@Service
@Slf4j
public class ManageUserServiceImpl extends ServiceImpl<ManageUserMapper, User> implements IManageUserService {

    @Autowired
    private ManageUserMapper manageUserMapper;

    @Override
    public Boolean deleteUser(String userId) {
        Long longUserId = Long.valueOf(userId);
        int delete1 = manageUserMapper.delete(MPUtil.queryWrapperEq("id", longUserId));
        int delete2 = manageUserMapper.delete(MPUtil.queryWrapperEq("parent", longUserId));
        return delete1 > 0 && delete2 > 0;
    }

    @Override
    public RPage<User> getUserList(String keyword, Integer pageNum, Integer size) {
        Long longKeyword = -1L;
        try {
            longKeyword = Long.valueOf(keyword);
        }catch (Exception e){
            throw e;
        }finally {
            List<User> userList;
            RPage<User> userRPage;
            if (!Objects.equals(keyword, "") && keyword!=null){
                userList = manageUserMapper.selectUserListByName((pageNum-1)*size,  size, keyword, longKeyword);
                userRPage = new RPage<>(pageNum,size,userList);
                userRPage.setTotalCountAndTotalPage(manageUserMapper.selectCount(MPUtil.queryWrapperEq("username",keyword, "id",longKeyword)));
            }else {
                userList = manageUserMapper.selectUserList((pageNum-1)*size,  size);
                userRPage = new RPage<>(pageNum,size,userList);
                userRPage.setTotalCountAndTotalPage(manageUserMapper.selectAllCount());
            }
            return userRPage;
        }
    }

    @Override
    public RPage<User> getSubUsers(String userId, String keyword, Integer pageNum, Integer size) {
        Long longKeyword = -1L;
        try {
            longKeyword = Long.valueOf(keyword);
        }catch (Exception e){
            throw e;
        }finally {

            Long longUserId = Long.valueOf(userId);

            List<User> userList = manageUserMapper.selectSubUsers((pageNum - 1) * size, size, longUserId, keyword, longKeyword);
            RPage<User> userRPage = new RPage<>(pageNum, size, userList);

            userRPage.setTotalCountAndTotalPage(manageUserMapper.selectCount(MPUtil.queryWrapperEq("parent", longUserId, "id", longKeyword, "username", keyword)));

            return userRPage;
        }
    }
}
