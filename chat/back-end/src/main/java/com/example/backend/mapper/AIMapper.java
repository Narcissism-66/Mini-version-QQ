package com.example.backend.mapper;

import com.example.backend.entity.AIChat;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AIMapper {

    @Insert("INSERT INTO ai_chat(userId,question,reply,time) VALUES (#{userId},#{question},#{reply},#{time})")
    int AddAiChat(AIChat aiChat);

    @Select("SELECT * FROM ai_chat WHERE userId=#{userId}")
    List<AIChat> getAIChatByUserId(int userId);
}
