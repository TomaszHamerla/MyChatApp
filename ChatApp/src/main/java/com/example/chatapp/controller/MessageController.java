package com.example.chatapp.controller;

import com.example.chatapp.model.message.MessageRequest;
import com.example.chatapp.model.message.MessageResponse;
import com.example.chatapp.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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

    @PatchMapping("/chat/{chatId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void setMessageToSeen(@PathVariable("chatId") Long chatId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization").substring(7);
        messageService.setMessagesToSeen(chatId, jwt);
    }

    @PostMapping("/upload")
    public MessageResponse uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("messageRequest") String messageRequest
                           ) throws IOException {
        MessageRequest message = new ObjectMapper().readValue(messageRequest, MessageRequest.class);
        return messageService.uploadFile(file, message);
    }
}
