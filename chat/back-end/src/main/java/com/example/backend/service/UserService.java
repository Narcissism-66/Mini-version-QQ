package com.example.backend.service;

import com.example.backend.entity.User;

import java.util.List;

public interface UserService {
    User login(String account , String password);
    User getUserById(int id);
    List<User> getAllUser();
    String getAvatar(int id);
    void updateAvatar(int id,String avatar);
    int updateUserInfo(int id,String username, String OldPassword,String password);

}
