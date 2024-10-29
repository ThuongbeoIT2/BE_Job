package com.example.oauth2.ChatAI.controller;


import com.example.oauth2.ChatAI.dto.ChatMessage;
import com.example.oauth2.ChatAI.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {
    @Autowired
    private GeminiService geminiService;
    @MessageMapping("/chat/{userId}")
    @SendTo("/topic/{userId}")
    public ChatMessage chat(@DestinationVariable String userId, ChatMessage message) {
        System.out.println(message);
        String response = geminiService.chat(message.getMessage());
        System.out.println(response);
        return new ChatMessage(response, "CHATBOT");
    }
}
