package com.example.backend.controller.Story;


import com.example.backend.entity.RestBean;
import com.example.backend.service.LikeService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/like")
public class LikeController {
    @Resource
    LikeService likeService;

    @PostMapping("/handleLike")
    public RestBean<Integer> handleLike(@RequestParam("storyId") int storyId,
                                      HttpServletRequest request){
        int userId = (int) request.getAttribute("id");
        int status= likeService.handleLike(userId, storyId);
        if (status==1) return RestBean.success("点赞成功",1);
        else return RestBean.success("取消点赞",0);
    }

//    @PostMapping("/checkLike")
//    public RestBean<Integer> checkLike(@RequestParam("storyId") int storyId, HttpServletRequest request){
//        int userId = (int) request.getAttribute("id");
//        int status= likeService.checkLike(userId, storyId);
//        if (status==0) return RestBean.success("未点赞",0);
//        else return RestBean.success("已点赞",1);
//    }

}
