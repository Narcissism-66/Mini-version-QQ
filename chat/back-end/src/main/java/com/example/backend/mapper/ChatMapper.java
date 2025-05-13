package com.example.backend.mapper;

import com.example.backend.entity.*;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface ChatMapper {
    //发送信息
    @Insert("INSERT INTO friend_chat(sender,toUserId,content,time,IsRead) VALUES (#{sender},#{toUserId},#{content},#{time},#{IsRead})")
    Integer AddChat(FriendChat friendChat);

    //看到所有相关信息
    @Select("SELECT *FROM friend_chat WHERE (sender=#{sender} AND toUserId=#{toUserId}) OR (toUserId=#{sender} AND sender=#{toUserId})")
    List<FriendChat> GetChatById(Integer sender, Integer toUserId);

    //创建群聊
    @Insert("INSERT INTO group_information(userId,GroupName,avatar) VALUES (#{userId},#{groupName},#{avatar})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")//让数据库生成的主键值能够自动传回到 Java 对象中
    Integer CreateGroup(GroupInformation groupInformation);

    //加入群聊
    @Insert("INSERT INTO group_member(groupId,userId,time)VALUES (#{groupId},#{userId},#{time})")
    Integer JoinGroup(Integer groupId, Integer userId, Date time);

    //获取加入的群
    @Select("SELECT b.groupId,b.userId,a.GroupName,a.avatar,b.time FROM " +
            "group_information a,group_member b " +
            "WHERE a.id=b.groupId AND b.userId=#{userId}")
    List<GroupInformationAndUser> GetGroupById(Integer userId);

    //发送消息
    @Insert("INSERT INTO group_message(groupId,userId,content,time) VALUES (#{groupId},#{userId},#{content},#{time})")
    Integer SendMessageToGroup(GroupMessage groupMessage);

    //获取群里面的成员
    @Select("SELECT *FROM group_member WHERE groupId=#{groupId}")
    List<GroupMember> GetGroupMember(Integer groupId);

    //获取群里面的信息
    @Select("SELECT * FROM group_message WHERE groupId=#{groupId}")
    List<GroupMessage> GetGroupMessageByGroupId(Integer groupId);


    //通知--好友
    @Select("SELECT f1.* FROM friend_chat f1 " +
            "INNER JOIN (" +
            "   SELECT sender, MAX(time) AS max_time " +
            "   FROM friend_chat " +
            "   WHERE toUserId = #{toUserId} AND IsRead=false" +
            "   GROUP BY sender" +
            ") f2 ON f1.sender = f2.sender AND f1.time = f2.max_time " +
            "WHERE f1.toUserId = #{toUserId} " +
            "ORDER BY f1.time DESC")
    List<FriendChat> GetFriendChatByToUserId(Integer toUserId);

    //读取好友信息
    @Update("UPDATE friend_chat SET IsRead=true WHERE sender=#{sender} AND toUserId=#{toUserId}")
    Integer ReadFriendChat(Integer sender,Integer toUserId);
}
