package com.example.backend.service;

import com.example.backend.entity.Story;
import com.example.backend.entity.StoryAndUser;

import java.util.List;

public interface StoryService {
    int insertStory(Story story);
    List<StoryAndUser> getStoryByUserId(Integer id);
    List<StoryAndUser> getAllStory();
}
