package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GroupMessage {
    private Integer id,groupId,userId;
    private String content;
    private Date time;

    public GroupMessage(Integer groupId,Integer userId,String content,Date time)
    {
        this.groupId=groupId;
        this.userId=userId;
        this.content=content;
        this.time=time;
    }

    public GroupMessage(Integer id,Integer groupId,Integer userId,String content,Date time)
    {
        this.id=id;
        this.groupId=groupId;
        this.userId=userId;
        this.content=content;
        this.time=time;
    }
}
