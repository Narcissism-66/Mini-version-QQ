package com.example.backend.service.Impl;

import com.example.backend.entity.*;
import com.example.backend.mapper.ChatMapper;
import com.example.backend.mapper.UserMapper;
import com.example.backend.service.ChatService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    ChatMapper chatMapper;
    @Resource
    UserMapper userMapper;

    @Override
    public Integer AddChat(FriendChat friendChat) {
        return chatMapper.AddChat(friendChat);
    }

    @Override
    public List<FriendChat> GetChatById(Integer sender, Integer toUserId) {
        return chatMapper.GetChatById(sender, toUserId);
    }

    @Override
    public Integer CreateGroup(GroupInformation groupInformation) {
        chatMapper.CreateGroup(groupInformation);
        return groupInformation.getId();
    }

    @Override
    public Integer JoinGroup(Integer groupId, Integer userId, Date time) {
        return chatMapper.JoinGroup(groupId, userId, time);
    }

    @Override
    public List<GroupInformationAndUser> GetGroupById(Integer userId) {
        return chatMapper.GetGroupById(userId);
    }

    @Override
    public Integer SendMessageToGroup(GroupMessage groupMessage) {
        return chatMapper.SendMessageToGroup(groupMessage);
    }

    @Override
    public List<User> GetGroupMember(Integer groupId) {
        List<GroupMember> groupMembers=chatMapper.GetGroupMember(groupId);
        List<User> users=new ArrayList<>();
        for (GroupMember groupMember : groupMembers) {
//            System.out.println(groupMember.getUserId());
            userMapper.getUserById(groupMember.getUserId()).setPassword("*");
            users.add(userMapper.getUserById(groupMember.getUserId()));
        }
        return users;
    }

    @Override
    public List<GroupMessage> GetGroupMessageByGroupId(Integer groupId) {
        return chatMapper.GetGroupMessageByGroupId(groupId);
    }

    @Override
    public List<FriendChat> GetFriendChatByToUserId(Integer toUserId) {
        return chatMapper.GetFriendChatByToUserId(toUserId);
    }

    @Override
    public Integer ReadFriendChat(Integer sender,Integer toUserId) {
        return chatMapper.ReadFriendChat(sender,toUserId);
    }
}
