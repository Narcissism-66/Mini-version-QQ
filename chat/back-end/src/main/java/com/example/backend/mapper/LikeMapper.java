package com.example.backend.mapper;

import com.example.backend.entity.StoryLike;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LikeMapper {
    //点赞
    @Insert("INSERT INTO story_like(userId,storyId,time) VALUES (#{userId},#{storyId},#{time})")
    void addLike(StoryLike storyLike);

    //增加点赞数量
    @Update("UPDATE story SET likes = likes + 1 WHERE id = #{storyId}")
    void addLikeNum(int storyId);

    //检查是否已经点赞
    @Select("SELECT id FROM story_like WHERE userId = #{userId} AND storyId = #{storyId}")
    Integer checkLike(int userId, int storyId);

    //取消点赞
    @Delete("DELETE FROM story_like WHERE userId = #{userId} AND storyId = #{storyId}")
    void deleteLike(int userId, int storyId);

    //减少点赞数量
    @Update("UPDATE story SET likes = likes - 1 WHERE id = #{storyId}")
    void deleteLikeNum(int storyId);


}
