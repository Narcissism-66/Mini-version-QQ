package com.example.backend.service;

import com.example.backend.entity.StoryFavourite;

public interface FavouriteService {
//    int addFavourite(StoryFavourite storyFavourite);
    int handleFavourite(int userId, int storyId);
//    int deleteFavourite(int userId, int storyId);
//    int checkFavourite(int userId, int storyId);

}
