package com.example.chatapp.controller;

import com.example.chatapp.model.message.MessageRequest;
import com.example.chatapp.model.message.MessageResponse;
import com.example.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/chat/{chatId}")
    public List<MessageResponse> getAllMessages(
            @PathVariable("chatId") Long chatId
    ) {
        return messageService.findChatMessages(chatId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveMessage(@RequestBody MessageRequest message) {
        messageService.saveMessage(message);
    }
}
