package com.example.chatapp.model.message;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {

    private Long id;
    private String content;
    private Long senderId;
    private Long receiverId;
    private MessageType type;
    private LocalDateTime createdDate;
}
