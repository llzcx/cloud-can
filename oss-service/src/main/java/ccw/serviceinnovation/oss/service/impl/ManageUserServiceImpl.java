package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.oss.common.util.MPUtil;
import ccw.serviceinnovation.oss.mapper.ManageUserMapper;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.service.IManageUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.generic.MULTIANEWARRAY;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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
    public Boolean deleteUser(Long userId) {
        int delete = manageUserMapper.delete(MPUtil.queryWrapperEq("id", userId));
        return delete > 0;
    }

    @Override
    public RPage<User> getUserList(String userName, Integer pageNum, Integer size) {
        List<User> userList;
        RPage<User> userRPage;
        if (!Objects.equals(userName, "") && userName!=null){
            userList = manageUserMapper.selectUserListByName((pageNum-1)*size,  size, userName);
            userRPage = new RPage<>(pageNum,size,userList);
            userRPage.setTotalCountAndTotalPage(manageUserMapper.selectCount(MPUtil.queryWrapperEq("username",userName)));
        }else {
            userList = manageUserMapper.selectUserList((pageNum-1)*size,  size);
            userRPage = new RPage<>(pageNum,size,userList);
            userRPage.setTotalCountAndTotalPage(manageUserMapper.selectAllCount());
        }

        return userRPage;
    }

    @Override
    public RPage<User> getSubUsers(Long userId, Integer pageNum, Integer size) {
        List<User> userList = manageUserMapper.selectSubUsers((pageNum-1)*size,  size, userId);
        RPage<User> userRPage = new RPage<>(pageNum,size,userList);
        userRPage.setTotalCountAndTotalPage(manageUserMapper.selectCount(MPUtil.queryWrapperEq("parent",userId)));

        return userRPage;
    }
}
