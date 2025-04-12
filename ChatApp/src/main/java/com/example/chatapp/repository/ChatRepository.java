package com.example.chatapp.repository;

import com.example.chatapp.model.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT DISTINCT c FROM Chat c WHERE c.sender.id = :id OR c.recipient.id = :id ORDER BY c.createdDate DESC")
    List<Chat> findChatsBySenderId(Long id);

    @Query("""
            SELECT DISTINCT c FROM Chat c WHERE (c.sender.id = :senderId AND c.recipient.id = :receiverId) OR
             (c.sender.id = :receiverId AND c.recipient.id = :senderId) ORDER BY c.createdDate DESC
            """)
    Optional<Chat> findChatByReceiverAndSender(Long senderId, Long receiverId);
}