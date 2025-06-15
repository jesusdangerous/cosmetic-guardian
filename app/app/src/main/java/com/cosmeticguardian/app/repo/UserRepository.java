package com.cosmeticguardian.app.repo;

import com.cosmeticguardian.app.model.User;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByTelegramId(String telegramId);

    Optional<User> findByEmail(@Email String email);

    List<User> findByUsernameContainingIgnoreCase(String searchTerm);
}
