package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class StoryLike {
    private Integer id,userId,storyId;
    private Date time;

    public StoryLike(Integer userId,Integer storyId,Date time) {
        this.userId = userId;
        this.storyId = storyId;
        this.time = time;

    }
}
