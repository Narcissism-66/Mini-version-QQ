package com.example.backend.service;

import com.example.backend.entity.User;

import java.util.List;

public interface UserService {
    User login(String account , String password);
    User getUserById(int id);
    List<User> getAllUser();
    String getAvatar(int id);
}
