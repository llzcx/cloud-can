package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.oss.mapper.UserMapper;
import ccw.serviceinnovation.oss.pojo.vo.LoginVo;
import ccw.serviceinnovation.oss.pojo.vo.RPage;
import ccw.serviceinnovation.oss.pojo.vo.UserVo;
import ccw.serviceinnovation.oss.service.IUserService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ccw.serviceinnovation.oss.common.util.MPUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 陈翔
 */
@Service
@Transactional(rollbackFor={Exception.class,RuntimeException.class})
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public LoginVo login(String username, String password) {
        User user = userMapper.selectOne(MPUtil.queryWrapperEq("username", username,"password",password));
        if(user!=null){
            LoginVo login = new LoginVo();
            login.setToken(JwtUtil.sign(user.getId(), user.getUsername(), user.getPassword()));
            login.setAdmin(user.getAdmin());
            return login;
        }
        return null;
    }

    @Override
    public User register(String username, String password, String phone) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        String time = DateUtil.now();
        user.setCreateTime(time);
        user.setUpdateTime(time);
        user.setPhone(phone);
        user.setParent(null);
        userMapper.insert(user);
        return user;
    }

    @Override
    public User createRamUser(String username, String password, Long userId) {
        User oldUser = userMapper.selectOne(MPUtil.queryWrapperEq("username", username));
        if (oldUser!=null&&oldUser.getUsername().equals(username)){
            return null;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        String time = DateUtil.now();
        user.setCreateTime(time);
        user.setUpdateTime(time);
        user.setParent(userId);
        userMapper.insert(user);
        return user;
    }

    @Override
    public RPage<UserVo> getSubUsers(Long id, String keyword, Integer pageNum, Integer size) {
        List<User> users = userMapper.selectUsersByName(id,keyword,(pageNum-1)*size,  size);

        List<UserVo> userVos = new LinkedList<>();
        for (User user : users) {
            UserVo userVo = new UserVo();
            BeanUtil.copyProperties(user,userVo);
            userVos.add(userVo);
        }

        RPage<UserVo> userVoRPage = new RPage<>(pageNum,size,userVos);
        userVoRPage.setTotalCountAndTotalPage(userMapper.selectCountByName(id,keyword));

        return userVoRPage;
    }

    @Override
    public List<UserVo> deleteSubUser(Long userId, Long myId) {
        userMapper.delete(MPUtil.queryWrapperEq("id",userId));
        User user = userMapper.selectOne(MPUtil.queryWrapperEq("id", myId));
        List<UserVo> subUsers = new LinkedList<>();
        BeanUtil.copyProperties(user,subUsers);
        return subUsers;
    }


    @Override
    public Long getMainUserId(Long userId){
        Long parentUserId = userMapper.selectParentUser(userId);
        if (parentUserId==null){
            return userId;
        }else{
            return parentUserId;
        }
    }



}
