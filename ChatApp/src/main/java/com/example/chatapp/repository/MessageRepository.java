package com.example.chatapp.repository;

import com.example.chatapp.model.message.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId")
    Page<Message> findMessagesByChatId(Long chatId, Pageable pageable);
}
