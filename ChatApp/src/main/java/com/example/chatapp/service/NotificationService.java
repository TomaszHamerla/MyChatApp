package com.example.chatapp.service;

import com.example.chatapp.model.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendNotification(String userEmail, Notification notification) {
        messagingTemplate.convertAndSendToUser(userEmail, "/chat", notification);
    }
}
