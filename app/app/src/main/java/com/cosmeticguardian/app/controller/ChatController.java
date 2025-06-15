// ChatController.java
package com.cosmeticguardian.app.controller;

import com.cosmeticguardian.app.model.Chat;
import com.cosmeticguardian.app.model.Message;
import com.cosmeticguardian.app.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Chat>> getUserChats(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUserChats(userId));
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(
            @RequestParam Long userId,
            @RequestParam String title) {
        return ResponseEntity.ok(chatService.createChat(userId, title));
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Message> sendMessage(
            @PathVariable Long chatId,
            @RequestParam Long senderId,
            @RequestBody String content) {
        return ResponseEntity.ok(chatService.sendMessage(chatId, senderId, content));
    }

    @PostMapping("/{chatId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long chatId,
            @RequestParam Long userId) {
        chatService.markMessagesAsRead(chatId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<Message>> getChatMessages(@PathVariable Long chatId) {
        return ResponseEntity.ok(chatService.getChatMessages(chatId));
    }
}