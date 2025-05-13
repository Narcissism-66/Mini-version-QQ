package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class FriendChat {
    private Integer id,sender,toUserId;
    private String content;
    private Boolean IsRead;
    private Date time;

    public FriendChat(Integer sender,Integer toUserId,String content,Date time,Boolean IsRead){
        this.sender = sender;
        this.toUserId = toUserId;
        this.content = content;
        this.time = time;
        this.IsRead = IsRead;
    }
    public FriendChat(Integer id,Integer sender,Integer toUserId,String content,Date time,Boolean IsRead){
        this.id=id;
        this.sender = sender;
        this.toUserId = toUserId;
        this.content = content;
        this.time = time;
        this.IsRead = IsRead;
    }
}
