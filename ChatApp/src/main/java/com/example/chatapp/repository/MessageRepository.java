package com.example.chatapp.repository;

import com.example.chatapp.model.message.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId ORDER BY m.createdDate")
    List<Message> findMessagesByChatId(Long chatId);
}
