package com.example.oauth2.config;

import com.example.oauth2.ChatMessage;


import com.example.oauth2.notify.Notify;
import com.example.oauth2.notify.NotifyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;


import org.springframework.web.bind.annotation.RestController;


@RestController
public class WebSocketController {
    @Autowired
    private NotifyRepository notifyRepository;
    @MessageMapping("/chat/{roomID}")
    @SendTo("/topic/{roomID}")
    public ChatMessage chat(ChatMessage message){
        System.out.println(message.getMessage());
        return message;
    }
    @MessageMapping("/notify/{userID}")
    @SendTo("/topic/{userID}")
    public Notify notify(Notify notify){
        System.out.println(notify.toString());
        return notify;
    }


}


