package com.example.backend.controller;


import com.example.backend.entity.RestBean;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    UserService userService;

    @GetMapping("/information")
    public RestBean<User> getUserInfo(HttpServletRequest request){
        Integer userId=(Integer) request.getAttribute("id");
        User user=userService.getUserById(userId);
        return RestBean.success("cg",user);
    }

    @GetMapping("/AllUser")
    public RestBean<List<User>> getAllUser(){
        List<User> users=userService.getAllUser();
        return RestBean.success("cg",users);
    }

    @GetMapping("/getAvatar")
    public RestBean<String> getAvatarById(@RequestParam("id")Integer id){
        String avatar=userService.getAvatar(id);
        return RestBean.success("cg",avatar);
    }
}
