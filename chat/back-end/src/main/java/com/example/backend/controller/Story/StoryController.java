package com.example.backend.controller.Story;


import com.example.backend.entity.RestBean;
import com.example.backend.entity.Story;
import com.example.backend.entity.StoryAndUser;
import com.example.backend.service.StoryService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/story")
public class StoryController {

    @Resource
    StoryService storyService;

    @PostMapping("/insert")
    public RestBean<String> insertStory(@RequestParam("title") String title,
                                        @RequestParam("content") String content,
                                        HttpServletRequest request) {
        int userId = (int) request.getAttribute("id");
        int status=storyService.insertStory(new Story(userId, title, content, new Date()));
        if(status==1)return RestBean.success("cg");
        else return RestBean.failure(503,"sb");
    }

    @GetMapping("/getStoryByUserId")
    public RestBean<List<StoryAndUser>> getStoryByUserId(HttpServletRequest request) {
        int userId = (int) request.getAttribute("id");
        return RestBean.success("cg",storyService.getStoryByUserId(userId));
    }

}
