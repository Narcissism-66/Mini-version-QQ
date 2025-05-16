package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class StoryFavourite {
    private Integer id,userId,storyId;
    private Date time;

    public StoryFavourite(Integer userId,Integer storyId,Date time) {
        this.userId = userId;
        this.storyId = storyId;
        this.time = time;

    }
}
