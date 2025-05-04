package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class FriendChat {
    private Integer id,sender,toUserId;
    private String content;
    private Date time;

    public FriendChat(Integer sender,Integer toUserId,String content,Date time){
        this.sender = sender;
        this.toUserId = toUserId;
        this.content = content;
        this.time = time;
    }
    public FriendChat(Integer id,Integer sender,Integer toUserId,String content,Date time){
        this.id=id;
        this.sender = sender;
        this.toUserId = toUserId;
        this.content = content;
        this.time = time;
    }
}
