package com.example.backend.service.Impl;

import com.example.backend.entity.AIChat;
import com.example.backend.mapper.AIMapper;
import com.example.backend.service.AIService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIServiceImpl implements AIService {

    @Resource
    AIMapper aiMapper;

    @Override
    public int AddAiChat(AIChat aiChat) {
        return aiMapper.AddAiChat(aiChat);
    }

    @Override
    public List<AIChat> getAIChatByUserId(int userId) {
        return aiMapper.getAIChatByUserId(userId);
    }
}
