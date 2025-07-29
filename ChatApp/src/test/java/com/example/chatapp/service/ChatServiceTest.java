package com.example.chatapp.service;

import com.example.chatapp.exception.ResourceNotFoundException;
import com.example.chatapp.model.chat.Chat;
import com.example.chatapp.model.chat.ChatResponse;
import com.example.chatapp.model.user.User;
import com.example.chatapp.repository.ChatRepository;
import com.example.chatapp.repository.UserRepository;
import com.example.chatapp.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private ChatService chatService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .enabled(true)
                .build();
    }

    @Test
    void getChatsByReceiverIdShouldReturnChats() {
        // given
        String jwt = "mock-jwt-token";

        User recipient = User.builder()
                .id(2L)
                .email("recipient@example.com")
                .enabled(true)
                .build();

        Chat chat = mock(Chat.class);
        when(chat.getId()).thenReturn(1L);
        when(chat.getSender()).thenReturn(user);
        when(chat.getRecipient()).thenReturn(recipient);
        when(chat.getChatName(user.getId())).thenReturn("Test Chat");
        when(chat.getLastMessage()).thenReturn("Hello");
        when(chat.getUnreadMessages(user.getId())).thenReturn(0L);
        when(chat.getNickName(user.getId())).thenReturn("TestUser");

        List<Chat> chatList = List.of(chat);

        when(jwtService.extractUsername(jwt)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(chatRepository.findChatsBySenderId(user.getId())).thenReturn(chatList);

        // when
        List<ChatResponse> result = chatService.getChatsByReceiverId(jwt);

        // then
        assertEquals(1, result.size());

        ChatResponse response = result.get(0);
        assertEquals(1L, response.getId());
        assertEquals("Test Chat", response.getName());
        assertEquals("Hello", response.getLastMessage());
        assertEquals(1L, response.getSenderId());
        assertEquals(2L, response.getReceiverId());
        assertEquals(0L, response.getUnreadMessages());
        assertEquals("TestUser", response.getSenderNick());
    }

    @Test
    void getChatsByReceiverIdShouldThrowWhenUserNotFound() {
        // given
        String jwt = "invalid-jwt";
        String email = "nonexistent@example.com";

        when(jwtService.extractUsername(jwt)).thenReturn(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when + then
        assertThrows(UsernameNotFoundException.class, () -> {
            chatService.getChatsByReceiverId(jwt);
        });
    }
}