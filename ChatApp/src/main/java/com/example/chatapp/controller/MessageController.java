package com.example.chatapp.controller;

import com.example.chatapp.model.message.MessageRequest;
import com.example.chatapp.model.message.MessageResponse;
import com.example.chatapp.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("messages")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/chat/{chatId}")
    public Page<MessageResponse> getAllMessages(
            @PathVariable("chatId") Long chatId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return messageService.findChatMessages(chatId, page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveMessage(@RequestBody MessageRequest message) {
        messageService.saveMessage(message);
    }
}
