package com.example.backend.service;

import com.example.backend.entity.StoryLike;

public interface LikeService {
//    int addLike(StoryLike storyLike);
    int handleLike(int userId, int storyId);
//    int deleteLike(int userId, int storyId);
//    int checkLike(int userId, int storyId);

}
