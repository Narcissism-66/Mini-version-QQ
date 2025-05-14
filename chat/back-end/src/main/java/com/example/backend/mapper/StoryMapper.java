package com.example.backend.mapper;

import com.example.backend.entity.Story;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StoryMapper {

    //发表说说
    @Insert("INSERT INTO story(userId,title,content,time) VALUES (#{userId},#{title},#{content},#{time})")
    int insertStory(Story story);

    //获取用户的所有说说
    @Select("SELECT * FROM story WHERE userId = #{userId}")
    List<Story> getStoryByUserId(Integer userId);

    //获取所有的说说
    @Select("SELECT * FROM story")
    List<Story> getAllStory();

}
