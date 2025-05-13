package com.example.backend.controller.Chat;

import com.example.backend.entity.*;
import com.example.backend.service.ChatService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Resource
    ChatService chatService;

    @PostMapping("/AddChat")
    public RestBean<String> AddChat(@RequestParam("toUserId")Integer toUserId,
                                    @RequestParam("content") String content,
                                    HttpServletRequest request){
        Integer sender= (Integer) request.getAttribute("id");
        int status=chatService.AddChat(new FriendChat(sender,toUserId,content,new Date(),false));
        if(status==1){
            return  RestBean.success("success");
        }
        else return RestBean.failure(503,"失败");
    }

    @GetMapping("/GetChatById")
    public RestBean<List<FriendChat>> GetChatById(@RequestParam("toUserId")Integer toUserId,
                                                  HttpServletRequest request){
        Integer sender= (Integer) request.getAttribute("id");
        int status=chatService.ReadFriendChat(toUserId,sender);
        return RestBean.success("cg",chatService.GetChatById(sender,toUserId));
    }

    @PostMapping("/CreateGroup")
    public RestBean<String> CreateGroup(@RequestParam("groupName")String groupName,
                                        @RequestParam("avatar")String avatar,
                                        HttpServletRequest request){
        int userId= (int) request.getAttribute("id");
        int groupId=chatService.CreateGroup(new GroupInformation(userId,groupName,avatar));
        int status=chatService.JoinGroup(groupId,userId,new Date());
        if (status==1)return RestBean.success("success");
        else return RestBean.failure(503,"失败");
    }

    @PostMapping("/JoinGroup")
    public RestBean<String> JoinGroup(@RequestParam("groupId")Integer groupId,
                                      @RequestParam("userId")Integer userId)
    {
        int status=chatService.JoinGroup(groupId,userId,new Date());
        if (status==1)return RestBean.success("success");
        else return RestBean.failure(503,"失败");
    }


    @GetMapping("/GetGroupById")
    public RestBean<List<GroupInformationAndUser>> GetGroupById(HttpServletRequest request){
        int userId= (int) request.getAttribute("id");
        return RestBean.success("cg",chatService.GetGroupById(userId));
    }

    @PostMapping("/SendMessageToGroup")
    public RestBean<String> SendMessageToGroup(@RequestParam("groupId")Integer groupId,
                                               HttpServletRequest request,
                                               @RequestParam("content")String content) {
        int userId = (int) request.getAttribute("id");
        int status = chatService.SendMessageToGroup(new GroupMessage(groupId, userId, content, new Date()));
        if (status == 1) return RestBean.success("success");
        else return RestBean.failure(503, "失败");
    }

    @GetMapping("/GetGroupMember")
    public RestBean<List<User>> GetGroupMember(@RequestParam("groupId")Integer groupId){
        return RestBean.success("cg",chatService.GetGroupMember(groupId));
    }

    @GetMapping("/GetGroupMessageByGroupId")
    public RestBean<List<GroupMessage>> GetGroupMessageByGroupId(@RequestParam("groupId")Integer groupId){
        return RestBean.success("cg",chatService.GetGroupMessageByGroupId(groupId));
    }

    @GetMapping("/GetFriendChatByToUserId")
    public RestBean<List<FriendChat>> GetFriendChatByToUserId(HttpServletRequest request){
        Integer toUserId= (Integer) request.getAttribute("id");
        return RestBean.success("cg",chatService.GetFriendChatByToUserId(toUserId));
    }

    @PostMapping("/ReadFriendChat")
    public RestBean<String> ReadFriendChat(@RequestParam("sender") Integer sender,
                                           HttpServletRequest request){
        Integer toUserId= (Integer) request.getAttribute("id");
        int status=chatService.ReadFriendChat(sender,toUserId);
        return RestBean.success("success");
    }
}
