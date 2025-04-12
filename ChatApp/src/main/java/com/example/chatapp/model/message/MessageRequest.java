package com.example.chatapp.model.message;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageRequest {

    private String content;
    private Long senderId;
    private Long receiverId;
    private Long chatId;
}
