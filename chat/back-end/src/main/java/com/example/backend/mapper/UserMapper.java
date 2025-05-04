package com.example.backend.mapper;

import com.example.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM  user WHERE account=#{account} AND password=#{password}")
    User login(String account , String password);

    @Select("SELECT *FROM user WHERE id=#{id}")
    User getUserById(int id);

    @Select("SELECT *FROM user ")
    List<User> getAllUser();

    @Select("SELECT avatar FROM user WHERE id=#{id}")
    String getAvatar(int id);
}
