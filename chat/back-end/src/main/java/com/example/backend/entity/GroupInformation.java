package com.example.backend.entity;

import lombok.Data;

@Data
public class GroupInformation {
    private Integer id,userId;
    private String GroupName,avatar;

    public GroupInformation(Integer userId, String groupName, String avatar) {
        this.userId = userId;
        this.GroupName = groupName;
        this.avatar = avatar;
    }
}
