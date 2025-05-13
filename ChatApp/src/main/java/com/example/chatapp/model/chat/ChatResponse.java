package com.example.chatapp.model.chat;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {

    private long id;
    private String name;
    private String lastMessage;
    private long senderId;
    private long receiverId;
    private long unreadMessages;
    private String senderNick;
}
