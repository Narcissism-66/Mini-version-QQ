package com.example.backend.service.Impl;

import com.example.backend.entity.User;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    UserMapper userMapper;

    @Override
    public User login(String account, String password) {
        return userMapper.login(account,password);
    }

    @Override
    public User getUserById(int id) {
        return userMapper.getUserById(id);
    }

    @Override
    public List<User> getAllUser() {
        return userMapper.getAllUser();
    }

    @Override
    public String getAvatar(int id) {
        return userMapper.getAvatar(id);
    }

    @Override
    public void updateAvatar(int id, String avatar) {
        userMapper.updateAvatar(id,avatar);
    }

    @Override
    public int updateUserInfo(int id, String username,String OldPassword,String NewPassword) {
        if (userMapper.verifyPassword(id).equals(OldPassword))
        {
            userMapper.updateUserInfo(id,username,NewPassword);
            return 1;
        }
        else return 0;

    }
}
