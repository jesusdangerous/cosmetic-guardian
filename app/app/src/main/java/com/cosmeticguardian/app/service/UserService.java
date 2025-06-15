package com.cosmeticguardian.app.service;

import com.cosmeticguardian.app.model.User;
import com.cosmeticguardian.app.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    @Transactional
    public User createUser(String username, String avatarUrl) {
        User user = new User();
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long userId, String username, String avatarUrl) {
        User user = getUserById(userId);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String searchTerm) {
        return userRepository.findByUsernameContainingIgnoreCase(searchTerm);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public boolean existsById(Long userId) {
        return userRepository.existsById(userId);
    }
}