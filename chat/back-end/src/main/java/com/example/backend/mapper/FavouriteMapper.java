package com.example.backend.mapper;

import com.example.backend.entity.StoryFavourite;
import com.example.backend.entity.StoryLike;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface FavouriteMapper {
    //收藏
    @Insert("INSERT INTO story_favourite(userId,storyId,time) VALUES (#{userId},#{storyId},#{time})")
    void addFavourite(StoryFavourite storyFavourite);

    //增加收藏数量
    @Update("UPDATE story SET favourites = favourites + 1 WHERE id = #{storyId}")
    void addFavouriteNum(int storyId);

    //检查是否已经收藏过
    @Select("SELECT id FROM story_favourite WHERE userId = #{userId} AND storyId = #{storyId}")
    Integer checkFavourite(int userId, int storyId);

    //取消收藏
    @Insert("DELETE FROM story_favourite WHERE userId = #{userId} AND storyId = #{storyId}")
    void deleteFavourite(int userId, int storyId);

    //减少收藏数量
    @Update("UPDATE story SET favourites = favourites - 1 WHERE id = #{storyId}")
    void deleteFavouriteNum(int storyId);


}
