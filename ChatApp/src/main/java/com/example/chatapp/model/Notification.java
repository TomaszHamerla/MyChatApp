package com.example.chatapp.model;

import com.example.chatapp.model.message.MessageType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    private Long chatId;
    private String content;
    private Long senderId;
    private Long receiverId;
    private String chatName;
    private MessageType type;
    private LocalDateTime createdDate;
}
