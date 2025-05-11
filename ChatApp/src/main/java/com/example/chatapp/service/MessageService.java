package com.example.chatapp.service;

import com.example.chatapp.exception.ResourceNotFoundException;
import com.example.chatapp.model.chat.Chat;
import com.example.chatapp.model.Notification;
import com.example.chatapp.model.message.Message;
import com.example.chatapp.model.message.MessageRequest;
import com.example.chatapp.model.message.MessageResponse;
import com.example.chatapp.repository.ChatRepository;
import com.example.chatapp.repository.MessageRepository;
import com.example.chatapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Transactional
    public void saveMessage(MessageRequest messageRequest) {
        Chat chat = chatRepository.findById(messageRequest.getChatId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));

        Message message = new Message();
        message.setContent(messageRequest.getContent());
        message.setChat(chat);
        message.setSenderId(messageRequest.getSenderId());
        message.setReceiverId(messageRequest.getReceiverId());

        messageRepository.save(message);

        Notification notification = Notification.builder()
                .chatId(chat.getId())
                .content(messageRequest.getContent())
                .senderId(messageRequest.getSenderId())
                .receiverId(messageRequest.getReceiverId())
                .chatName(chat.getRecipient().getUsername())
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
                .content(message.getContent())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .createdDate(message.getCreatedDate())
                .build());
    }
}
