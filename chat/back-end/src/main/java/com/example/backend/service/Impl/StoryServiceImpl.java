package com.example.backend.service.Impl;

import com.example.backend.entity.Story;
import com.example.backend.entity.StoryAndUser;
import com.example.backend.entity.User;
import com.example.backend.mapper.StoryMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.StoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class StoryServiceImpl implements StoryService {
    @Resource
    StoryMapper storyMapper;
    @Resource
    UserMapper userMapper;

    @Override
    public int insertStory(Story story) {
        return storyMapper.insertStory(story);
    }

    @Override
    public List<StoryAndUser> getStoryByUserId(Integer id) {
        List<StoryAndUser> storyList = new ArrayList<>();
        List<Story> story = storyMapper.getStoryByUserId(id);
        User user=userMapper.getUserById(id);
        for (Story s : story) {
            storyList.add(new StoryAndUser(s.getUserId(),s.getLikes(),s.getFavourites(),s.getTitle(),s.getContent(),user.getAvatar(),user.getIsLike(),user.getIsFavourite(),s.getTime()));
        }
        return storyList;
    }

    @Override
    public List<StoryAndUser> getAllStory() {
        List<StoryAndUser> storyList = new ArrayList<>();
        List<Story> story = storyMapper.getAllStory();

        for (Story s : story) {
            User user=userMapper.getUserById(s.getUserId());
            storyList.add(new StoryAndUser(s.getUserId(),s.getLikes(),s.getFavourites(),s.getTitle(),s.getContent(),user.getAvatar(),user.getIsLike(),user.getIsFavourite(),s.getTime()));
        }
        return storyList;
    }
}
