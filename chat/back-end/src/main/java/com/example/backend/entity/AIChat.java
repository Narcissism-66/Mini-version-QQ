package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AIChat {
    private Integer id,userId;
    private String question,reply;
    private Date time;

    public AIChat(Integer userId,String question,String reply,Date time){
        this.userId = userId;
        this.question=question;
        this.reply=reply;
        this.time=time;
    }
    public AIChat(Integer id,Integer userId,String question,String reply,Date time){
        this.id=id;
        this.userId = userId;
        this.question=question;
        this.reply=reply;
        this.time=time;
    }
}
