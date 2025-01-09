package com.example.devsecops.controller;

import com.example.devsecops.model.ChatMessage;
import com.example.devsecops.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/messages")
public class ChatController {

    @Autowired
    private MessageService messageService;


    @GetMapping
    @PreAuthorize("")
    public List<ChatMessage> getMessages(@RequestParam String receiver) {
        return messageService.getPrivateMessages(receiver);
    }



    @MessageMapping("/sendMessage")
    @SendTo({"/topic/messages"})
    @PreAuthorize("")
    public ChatMessage sendMessage(ChatMessage chatMessage) {

        messageService.saveMessage(chatMessage);

        return chatMessage;
    }



    @GetMapping("/receivers")
    @PreAuthorize("")
    public List<Map<String, String>> getUsersByConversation() {
        return messageService.getUsersByConversation();
    }

}
