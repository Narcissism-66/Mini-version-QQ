package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Story {
    private Integer id,userId,likes,favourites;
    private String title,content;
    private Date time;

    public Story(Integer userId, String title, String content,Date time) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.time = time;
    }

    public Story(Integer id, Integer userId, String title, String content,Date time, Integer likes, Integer favourites) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.time = time;
        this.likes = likes;
        this.favourites = favourites;
    }
}
