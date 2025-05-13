package com.example.backend.service;

import com.example.backend.entity.AIChat;

import java.util.List;

public interface AIService {
    int AddAiChat(AIChat aiChat);
    List<AIChat> getAIChatByUserId(int userId);
}
