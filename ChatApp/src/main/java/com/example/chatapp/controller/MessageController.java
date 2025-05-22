package com.example.chatapp.controller;

import com.example.chatapp.model.message.Message;
import com.example.chatapp.model.message.MessageRequest;
import com.example.chatapp.model.message.MessageResponse;
import com.example.chatapp.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

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

    @GetMapping("/downloadFile/{messageId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long messageId) {
        Message message = messageService.findMessageById(messageId);
        String fileName = message.getFileName();
        String fileContent = message.getContent();
        byte[] decodedBytes = Base64.getDecoder().decode(fileContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(decodedBytes.length);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename(fileName)
                .build());

        return new ResponseEntity<>(decodedBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/image/{messageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long messageId) {
        Message message = messageService.findMessageById(messageId);
        String fileName = message.getFileName();
        String fileContent = message.getContent();
        byte[] decodedBytes = Base64.getDecoder().decode(fileContent);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(decodedBytes.length);
        headers.setContentType(getMimeType(fileName));
        headers.setContentDisposition(ContentDisposition.builder("inline")
                .filename(fileName)
                .build());

        return new ResponseEntity<>(decodedBytes, headers, HttpStatus.OK);
    }

    private MediaType getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        return switch (extension) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "png" -> MediaType.IMAGE_PNG;
            case "bmp", "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
            case "gif" -> MediaType.IMAGE_GIF;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }
}
