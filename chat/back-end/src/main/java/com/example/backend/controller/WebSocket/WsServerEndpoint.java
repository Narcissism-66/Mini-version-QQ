package com.example.backend.controller.WebSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


//监听websocket地址（/myWs）
@ServerEndpoint("/myWs")
@Component
@Slf4j
public class WsServerEndpoint {

    static Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    // 建立连接
    @OnOpen
    public void onOpen(Session session) {
        // 获取前端传递的 userId 参数
        String userId = session.getRequestParameterMap().get("userId").getFirst();
        sessionMap.put(userId, session); // 使用 userId 作为键
        log.info("websocket is open, userId: {}",userId);
    }

    // 收到客户端消息
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到WebSocket消息: {}", message);
        try {
            // 尝试解析 JSON 消息
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> msgMap = mapper.readValue(message, Map.class);
            log.info("解析后的消息内容: {}", msgMap);

            String toUserId = String.valueOf(msgMap.get("toUserId")); // 将数字转换为字符串
            String content = (String) msgMap.get("content");
            String fromUserId = session.getRequestParameterMap().get("userId").getFirst();

            log.info("处理消息 - 发送者: {}, 接收者: {}, 内容: {}", fromUserId, toUserId, content);
            if (sessionMap.containsKey(toUserId)) {
                Session targetSession = sessionMap.get(toUserId);
                targetSession.getBasicRemote().sendText("用户 " + fromUserId + " 对你说: " + content);
                log.info("消息已发送给用户: {}", toUserId);
            } else {
                // 可选：通知发送者目标用户不在线
                session.getBasicRemote().sendText("用户 " + toUserId + " 不在线");
                log.info("目标用户不存在: {}", toUserId);
            }
        } catch (Exception e) {
            log.error("消息处理失败: {}", e.getMessage());
            e.printStackTrace(); // 打印完整的错误堆栈
            // 如果不是JSON格式，则广播消息给所有用户
            String fromUserId = session.getRequestParameterMap().get("userId").getFirst();
            String broadcastMessage = "用户 " + fromUserId + " 广播消息: " + message;
            broadcast(broadcastMessage);
            log.info("收到非JSON格式消息，已广播: {}", message);
        }
    }

    // 关闭连接
    @OnClose
    public void onClose(Session session) {
        // 获取前端传递的 userId 参数
        String userId = session.getRequestParameterMap().get("userId").getFirst();
        sessionMap.remove(userId); // 使用 userId 移除对应的 Session
        log.info("websocket is close, userId: {}", userId);
    }

    // 广播消息给所有客户端
    private void broadcast(String message) {
        sessionMap.forEach((id, session) -> {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    log.error("广播消息失败: {}", e.getMessage());
                }
            }
        });
    }

    //定时任务，每隔两秒像客户端发送一个心跳
//    @Scheduled(fixedRate = 2000)
//    public void sendMessage() throws IOException {
//        for(String key:sessionMap.keySet()){
//            sessionMap.get(key).getBasicRemote().sendText("心跳");
//        }
//    }
}
