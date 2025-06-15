package com.cosmeticguardian.app.repo;

import com.cosmeticguardian.app.model.Chat;
import com.cosmeticguardian.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT c FROM Chat c WHERE c.user = :user AND c.isUnread = true")
    List<Chat> findUnreadChats(@Param("user") User user);
}