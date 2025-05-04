package com.example.backend.entity;

import lombok.Data;

import java.util.Date;

@Data
public class GroupInformationAndUser {
    private Integer groupId,userId;
    private Date time;
    private String avatar,GroupName;

    public GroupInformationAndUser(Integer groupId,Integer userId, String GroupName, String avatar,Date time) {
        this.groupId = groupId;
        this.userId = userId;
        this.GroupName = GroupName;
        this.time = time;
        this.avatar = avatar;
    }
}
