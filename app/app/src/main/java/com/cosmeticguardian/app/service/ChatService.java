package com.cosmeticguardian.app.service;

import com.cosmeticguardian.app.model.Chat;
import com.cosmeticguardian.app.model.Message;
import com.cosmeticguardian.app.model.User;
import com.cosmeticguardian.app.repo.ChatRepository;
import com.cosmeticguardian.app.repo.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final UserService userService;

    public List<Chat> getUserChats(Long userId) {
        User user = userService.getUserById(userId);
        return chatRepository.findByUserOrderByCreatedAtDesc(user);
    }

    @Transactional
    public Chat createChat(Long userId, String title) {
        User user = userService.getUserById(userId);

        Chat chat = new Chat();
        chat.setUser(user);
        chat.setTitle(title);

        return chatRepository.save(chat);
    }

    @Transactional
    public Message sendMessage(Long chatId, Long senderId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        User sender = userService.getUserById(senderId);

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);

        chat.setLastMessage(content);
        chatRepository.save(chat);

        return messageRepository.save(message);
    }

    @Transactional
    public void markMessagesAsRead(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        User user = userService.getUserById(userId);

        chatRepository.save(chat);
    }
}