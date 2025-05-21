package com.example.chatapp.service;

import com.example.chatapp.exception.ResourceNotFoundException;
import com.example.chatapp.model.Notification;
import com.example.chatapp.model.chat.Chat;
import com.example.chatapp.model.message.*;
import com.example.chatapp.model.user.User;
import com.example.chatapp.repository.ChatRepository;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public void saveMessage(MessageRequest messageRequest) {
        Chat chat = chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setChat(chat);
        message.setSenderId(messageRequest.getSenderId());
        message.setReceiverId(messageRequest.getReceiverId());
        message.setType(MessageType.TEXT);
        message.setState(MessageState.SENT);

        Message savedMsg = messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .content(messageRequest.getContent())
                .senderId(messageRequest.getSenderId())
                .receiverId(messageRequest.getReceiverId())
                .chatName(chat.getRecipient().getUsername())
                .createdDate(savedMsg.getCreatedDate())
                .type(MessageType.TEXT)
                .build();

        String userEmail = userRepository.findById(messageRequest.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getEmail();
        notificationService.sendNotification(userEmail, notification);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<MessageResponse> findChatMessages(Long chatId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Message> messagesPage = messageRepository.findMessagesByChatId(chatId, pageable);
        return messagesPage.map(message -> MessageResponse.builder()
                .id(message.getId())
                .content(getContent(message))
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .createdDate(message.getCreatedDate())
                .type(message.getType())
                .build());
    }

    @Transactional
    public void setMessagesToSeen(Long chatId, String jwt) {
        User user = getUserByJwt(jwt);
        Long senderId = user.getId();

        messageRepository.setMessagesToSeenByChatIdAndSenderId(chatId, senderId, MessageState.SEEN);
    }

    @Transactional
    public MessageResponse uploadFile(MultipartFile file, MessageRequest messageRequest) throws IOException {
        Chat chat = chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        String originalFilename = file.getOriginalFilename();
        byte[] fileBytes = file.getBytes();
        String encodedFile = Base64.getEncoder().encodeToString(fileBytes);

        Message message = new Message();
        message.setContent(encodedFile);
        message.setFileName(originalFilename);
        message.setChat(chat);
        message.setSenderId(messageRequest.getSenderId());
        message.setReceiverId(messageRequest.getReceiverId());
        message.setType(MessageType.FILE);
        message.setState(MessageState.SENT);

        Message savedMsg = messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .content(originalFilename)
                .senderId(messageRequest.getSenderId())
                .receiverId(messageRequest.getReceiverId())
                .chatName(chat.getRecipient().getUsername())
                .createdDate(savedMsg.getCreatedDate())
                .type(MessageType.FILE)
                .build();

        String userEmail = userRepository.findById(messageRequest.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"))
                .getEmail();
        notificationService.sendNotification(userEmail, notification);

        return MessageResponse.builder()
                .id(savedMsg.getId())
                .content(originalFilename)
                .senderId(messageRequest.getSenderId())
                .receiverId(messageRequest.getReceiverId())
                .createdDate(savedMsg.getCreatedDate())
                .type(MessageType.FILE)
                .build();
    }

    public Message findMessageById(Long messageId) {
        return this.messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));
    }

    private String getContent(Message message) {
        if (message.getType() == MessageType.FILE) {
            return message.getFileName();
        } else {
            return message.getContent();
        }
    }

    private User getUserByJwt(String jwt) {
        String userEmail = jwtService.extractUsername(jwt);
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
