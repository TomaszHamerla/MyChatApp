package com.example.chatapp.repository;

import com.example.chatapp.model.message.Message;
import com.example.chatapp.model.message.MessageState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId")
    Page<Message> findMessagesByChatId(Long chatId, Pageable pageable);

    @Modifying
    @Query("UPDATE Message SET state = :newState WHERE chat.id = :chatId AND receiverId = :senderId")
    void setMessagesToSeenByChatIdAndSenderId(Long chatId, Long senderId, MessageState newState);
}
