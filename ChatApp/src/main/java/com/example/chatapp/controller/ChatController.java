package com.example.chatapp.controller;

import com.example.chatapp.model.chat.ChatResponse;
import com.example.chatapp.service.ChatService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("chats")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public Long createChat(
            HttpServletRequest request,
            @RequestParam(name = "receiverId") Long receiverId
    ) {
        String jwt = request.getHeader("Authorization").substring(7);
        return chatService.createChat(jwt, receiverId);
    }

    @GetMapping
    public List<ChatResponse> getChatsByReceiver(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization").substring(7);
        return chatService.getChatsByReceiverId(jwt);
    }
}
