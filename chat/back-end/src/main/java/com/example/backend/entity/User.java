package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class User {
    private Integer id;
    private String username,account,avatar,password;
    private Boolean IsLike,IsFavourite;

    public User(Integer id,String username,String account,String avatar,String password,Boolean IsLike,Boolean IsFavourite)
    {
        this.id=id;
        this.username=username;
        this.account=account;
        this.avatar=avatar;
        this.password=password;
        this.IsLike=IsLike;
        this.IsFavourite=IsFavourite;
    }
}
