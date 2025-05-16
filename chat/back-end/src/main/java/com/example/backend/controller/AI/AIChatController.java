package com.example.backend.controller.AI;


import com.example.backend.entity.AIChat;
import com.example.backend.entity.RestBean;
import com.example.backend.service.AIService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;





@RestController
@RequestMapping("/api/AI/")
public class AIChatController {

    @Resource
    AIService aiService;

    private final ChatClient chatClient;


    @Autowired
    ChatMemory chatMemory;
    @Autowired
    ChatModel chatModel;

//    @Autowired
//    private JdbcTemplate jdbcTemplate;


//    @Autowired
//    JdbcChatMemoryRepository chatMemoryRepository;

    public AIChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    //获取之前的对话记录
    @GetMapping("/getAIChatByUserId")
    public RestBean<List<AIChat>> getAIChatByUserId(@RequestParam("userId") Integer userId) {
        return RestBean.success("cg",aiService.getAIChatByUserId(userId));
    }


    //最简单的问答
    @GetMapping("/chat1")
    public String chat(@RequestParam("message") String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }

    //流式输出
    @GetMapping(value = "/chat2", produces = MediaType.TEXT_EVENT_STREAM_VALUE )
    public Flux<String> chat2(@RequestParam("message") String message) {
        return chatClient.prompt()
                .user(message)
                .stream()
                .content()
                .delayElements(Duration.ofMillis(100));//延迟
    }

    //流式+窗口记忆化
    @GetMapping(value = "/chat3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat3(@RequestParam("message") String message) {
        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(10)//设置窗口的大小
                .build();

        Message userMessage = new UserMessage(message);//因为存储的消息需要是Message的而不是字符串，需要转换一下
        memory.add("test", userMessage);

        StringBuilder aiReplyBuilder = new StringBuilder();//字符串拼接

        Flux<String> aiResponseFlux = chatClient.prompt()
                .messages(memory.get("test"))//根据记忆进行回复
                .stream()
                .content()
                .doOnNext(aiReplyBuilder::append) // 每收到一段就拼接
                .doOnComplete(() -> {
                    // 流式结束后，把完整回复存入memory
                    String fullReply = aiReplyBuilder.toString();
                    Message aiMessage = new AssistantMessage(fullReply);
                    memory.add("test", aiMessage);//记忆化存储
                });
        return aiResponseFlux.delayElements(Duration.ofMillis(100));
    }

    //永久存储+流式输出+永久记忆
    @GetMapping(value="/chat4", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chat4(@RequestParam("message") String message,
                            @RequestParam("userId") Integer userId) {


        MessageWindowChatMemory memory = MessageWindowChatMemory.builder()
                .maxMessages(20)//设置窗口的大小
                .build();


        Message userMessage = new UserMessage(message);
        chatMemory.add(userId.toString(), userMessage);

        StringBuilder aiReplyBuilder = new StringBuilder();

        // 获取历史消息并生成回复
        Flux<String> aiResponseFlux = chatClient.prompt()
                .messages(chatMemory.get(userId.toString()))
                .stream()
                .content()
                .doOnNext(aiReplyBuilder::append)
                .doOnComplete(() -> {
                    // 流式结束后，保存完整对话到数据库
                    String fullReply = aiReplyBuilder.toString();
                    Message aiMessage = new AssistantMessage(fullReply);
                    chatMemory.add(userId.toString(), aiMessage);
                    aiService.AddAiChat(new AIChat(userId, message, fullReply,new Date()));
                });

        return aiResponseFlux.delayElements(Duration.ofMillis(100));
    }
}
