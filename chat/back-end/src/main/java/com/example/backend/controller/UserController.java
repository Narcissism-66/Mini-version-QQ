package com.example.backend.controller;


import com.example.backend.entity.RestBean;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    UserService userService;

    @GetMapping("/information")
    public RestBean<User> getUserInfo(HttpServletRequest request){
        Integer userId=(Integer) request.getAttribute("id");
        User user=userService.getUserById(userId);
        user.setPassword("*");
        return RestBean.success("cg",user);
    }
    @GetMapping("/getUserById")
    public RestBean<User> getUserById(@RequestParam("id") Integer id){

        User user=userService.getUserById(id);
        user.setPassword("*");
        return RestBean.success("cg",user);
    }

    @GetMapping("/AllUser")
    public RestBean<List<User>> getAllUser(){
        List<User> users=userService.getAllUser();
        for(User user:users){
            user.setPassword("*");
        }
        return RestBean.success("cg",users);
    }

    @GetMapping("/getAvatar")
    public RestBean<String> getAvatarById(@RequestParam("id")Integer id){
        String avatar=userService.getAvatar(id);
        return RestBean.success("cg",avatar);
    }

    @Value("${file.upload-dir}") // 从配置文件中读取上传目录路径
    private String uploadDir;

    @PostMapping("/uploadImg")
    public RestBean<String> uploadImage(@RequestParam("image") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return RestBean.failure(400, "上传的文件为空");
            }

            // 创建上传目录（如果不存在）
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                if (!directory.mkdirs()) { // 创建目录
                    return RestBean.failure(500, "无法创建上传目录");
                }
            }

            // 生成唯一的文件名
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);

            // 将文件保存到服务器
            Files.copy(file.getInputStream(), filePath);

            // 返回成功响应，包含文件的相对路径
            String fileUrl = "uploads/" + fileName; // 相对路径
            return RestBean.success("文件上传成功", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return RestBean.failure(500, "文件上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/updateAvatar")
    public RestBean<String> updateAvatar(HttpServletRequest request,
                                        @RequestParam("avatar") String avatar)
    {
        int userId= (int) request.getAttribute("id");
        userService.updateAvatar(userId,avatar);
        return RestBean.success("cg");
    }

    @PostMapping("/updateUserInfo")
    public RestBean<String> updateUserInfo(HttpServletRequest request,
                                           @RequestParam("username") String username,
                                           @RequestParam("OldPassword") String OldPassword,
                                           @RequestParam("NewPassword") String NewPassword){
        int userId= (int) request.getAttribute("id");
        int status=userService.updateUserInfo(userId,username,OldPassword,NewPassword);
        if (status==1)return RestBean.success("cg");
        else return RestBean.failure(500,"原密码错误！");
    }

}
