package com.example.backend.controller.Story;

import com.example.backend.entity.RestBean;
import com.example.backend.service.FavouriteService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/like")
public class FavouriteController {

    @Resource
    FavouriteService favouriteService;

    @PostMapping("/handleFavourite")
    public RestBean<Integer> handleFavourite(@RequestParam("storyId") int storyId,
                                      HttpServletRequest request){
        int userId = (int) request.getAttribute("id");
        int status= favouriteService.handleFavourite(userId, storyId);
        if (status==1) return RestBean.success("收藏成功",1);
        else return RestBean.success("取消收藏",0);
    }

//    @PostMapping("/checkFavourite")
//    public RestBean<Integer> checkFavourite(@RequestParam("storyId") int storyId, HttpServletRequest request){
//        int userId = (int) request.getAttribute("id");
//        int status= favouriteService.checkFavourite(userId, storyId);
//        if (status==0) return RestBean.success("未收藏",0);
//        else return RestBean.success("已收藏",1);
//    }
}
