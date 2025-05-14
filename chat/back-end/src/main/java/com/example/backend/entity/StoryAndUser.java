package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class StoryAndUser {
    private Integer userId,likes,favourites;
    private String title,content,avatar;
    private Boolean IsLike,IsFavourite;
    private Date time;

    public StoryAndUser(Integer userId, Integer likes, Integer favourites, String title, String content,String avatar, Boolean isLike, Boolean isFavourite, Date time) {
        this.userId = userId;
        this.likes = likes;
        this.favourites = favourites;
        this.title = title;
        this.content = content;
        this.avatar = avatar;
        IsLike = isLike;
        IsFavourite = isFavourite;
        this.time = time;
    }
}
