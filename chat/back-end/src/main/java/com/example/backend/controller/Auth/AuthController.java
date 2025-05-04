package com.example.backend.controller.Auth;


import com.example.backend.JWT.JWTUtil;
import com.example.backend.entity.RestBean;
import com.example.backend.entity.User;
import com.example.backend.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    UserService userService;

    @PostMapping("/login")
    public RestBean<String> login(@RequestParam("account")String account,
                                  @RequestParam("password")String password)
    {
        User user=userService.login(account,password);

        if(user==null) return RestBean.failure(401,"账号或密码错误");
        else
        {
            user.setPassword("*");
            return RestBean.success("登录成功", JWTUtil.createToken(user));
        }
    }
}
