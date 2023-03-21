package ccw.serviceinnovation.oss.service.impl;

import ccw.serviceinnovation.common.entity.User;
import ccw.serviceinnovation.oss.common.util.JwtUtil;
import ccw.serviceinnovation.oss.mapper.UserMapper;
import ccw.serviceinnovation.oss.service.IUserService;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ccw.serviceinnovation.oss.common.util.MPUtil;

/**
 * @author 陈翔
 */
@Service
@Transactional(rollbackFor={Exception.class,RuntimeException.class})
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Override
    public User createRamUser(String username, String password, String phone, Long userId) {
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



    @Autowired
    UserMapper userMapper;

    @Override
    public String login(String username, String password) {
        User user = userMapper.selectOne(MPUtil.queryWrapperEq("username", username,"password",password));
        if(user!=null){
            return JwtUtil.sign(user.getId(), user.getUsername(), user.getPassword());
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


}
