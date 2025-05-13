package com.example.backend.mapper;

import com.example.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserMapper {
    //登陆验证
    @Select("SELECT * FROM  user WHERE account=#{account} AND password=#{password}")
    User login(String account , String password);

    //通过id获取用户信息
    @Select("SELECT *FROM user WHERE id=#{id}")
    User getUserById(int id);

    //获取全部的用户信息
    @Select("SELECT *FROM user ")
    List<User> getAllUser();

    //获取头像
    @Select("SELECT avatar FROM user WHERE id=#{id}")
    String getAvatar(int id);

    //更新头像
    @Update("UPDATE user SET avatar=#{avatar}  WHERE id=#{id}")
    void updateAvatar(int id,String avatar);

    //验证密码是否正确
    @Select("SELECT password FROM user WHERE id=#{id} ")
    String verifyPassword(int id);

    //更新信息
    @Update("UPDATE user SET username=#{username},password=#{password} WHERE id=#{id}")
    void updateUserInfo(int id,String username,String password);
}
