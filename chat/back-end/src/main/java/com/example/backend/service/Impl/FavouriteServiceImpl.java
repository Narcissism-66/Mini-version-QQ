package com.example.backend.service.Impl;

import com.example.backend.entity.StoryFavourite;
import com.example.backend.entity.StoryLike;
import com.example.backend.mapper.FavouriteMapper;
import com.example.backend.service.FavouriteService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FavouriteServiceImpl implements FavouriteService {

    @Resource
    FavouriteMapper favouriteMapper;



    @Override
    public int handleFavourite(int userId, int storyId) {
        Integer status= favouriteMapper.checkFavourite(userId, storyId);
        if (status==null){
            favouriteMapper.addFavourite(new StoryFavourite(userId, storyId,new Date()));
            favouriteMapper.addFavouriteNum(storyId);
            return 1;
        }
        else
        {
            favouriteMapper.deleteFavourite(userId, storyId);
            favouriteMapper.deleteFavouriteNum(storyId);
            return 0;
        }
    }



//    @Override
//    public int checkFavourite(int userId, int storyId) {
//        return favouriteMapper.checkFavourite(userId,storyId);
//    }


}
