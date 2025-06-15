package com.cosmeticguardian.app.repo;

import com.cosmeticguardian.app.model.Chat;
import com.cosmeticguardian.app.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatOrderBySentAtAsc(Chat chat);

    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND m.isRead = false AND m.sender != :currentUser")
    List<Message> findUnreadMessages(@Param("chat") Chat chat, @Param("currentUser") User currentUser);
}