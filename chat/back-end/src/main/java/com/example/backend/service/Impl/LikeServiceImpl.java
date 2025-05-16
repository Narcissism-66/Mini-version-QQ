package com.example.backend.service.Impl;

import com.example.backend.entity.StoryFavourite;
import com.example.backend.entity.StoryLike;
import com.example.backend.mapper.LikeMapper;
import com.example.backend.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LikeServiceImpl implements LikeService {

    @Resource
    LikeMapper likeMapper;



    @Override
    public int handleLike(int userId, int storyId) {
        Integer status= likeMapper.checkLike(userId, storyId);
        if (status==null){
            likeMapper.addLike(new StoryLike(userId, storyId,new Date()));
            likeMapper.addLikeNum(storyId);
            return 1;
        }
        else
        {
            likeMapper.deleteLike(userId, storyId);
            likeMapper.deleteLikeNum(storyId);
            return 0;
        }
    }


//    @Override
//    public int checkLike(int userId, int storyId) {
//        return likeMapper.checkLike(userId,storyId);
//    }


}
