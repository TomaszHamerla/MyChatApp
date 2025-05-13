package com.example.chatapp.service;

import com.example.chatapp.exception.ResourceNotFoundException;
import com.example.chatapp.model.chat.Chat;
import com.example.chatapp.model.chat.ChatResponse;
import com.example.chatapp.model.user.User;
import com.example.chatapp.repository.ChatRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<ChatResponse> getChatsByReceiverId(String jwt) {
        User user = getUserByJwt(jwt);
        Long userId = user.getId();
        return chatRepository.findChatsBySenderId(userId)
                .stream()
                .map(chat -> ChatResponse.builder()
                        .id(chat.getId())
                        .name(chat.getChatName(userId))
                        .lastMessage(chat.getLastMessage())
                        .senderId(chat.getSender().getId())
                        .receiverId(chat.getRecipient().getId())
                        .unreadMessages(chat.getUnreadMessages(userId))
                        .senderNick(chat.getNickName(userId))
                        .build())
                .toList();
    }

    @Transactional
    public Long createChat(String jwt, Long receiverId) {
        Long senderId = getUserByJwt(jwt).getId();
        Optional<Chat> existingChat = chatRepository.findChatByReceiverAndSender(senderId, receiverId);
        if (existingChat.isPresent()) {
            return existingChat.get().getId();
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + senderId + " not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + receiverId + " not found"));

        Chat chat = new Chat();
        chat.setSender(sender);
        chat.setRecipient(receiver);

        Chat savedChat = chatRepository.save(chat);
        return savedChat.getId();
    }

    @Transactional
    public void updateUserNick(Long userId, Long chatId, String newNick) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat not found"));
        if (chat.getSender().getId().equals(userId)) {
            chat.setSenderNickName(newNick);
        } else if (chat.getRecipient().getId().equals(userId)) {
            chat.setRecipientNickName(newNick);
        } else {
            throw new IllegalArgumentException("User is not part of this chat");
        }

        chatRepository.save(chat);
    }

    private User getUserByJwt(String jwt) {
        String userEmail = jwtService.extractUsername(jwt);
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
