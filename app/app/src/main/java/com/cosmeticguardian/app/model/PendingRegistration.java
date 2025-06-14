package com.cosmeticguardian.app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingRegistration {
    @Id
    private String telegramId;

    private String email;
    private String code;
    private LocalDateTime createdAt;
}