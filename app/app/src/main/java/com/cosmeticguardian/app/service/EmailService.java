package com.cosmeticguardian.app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Код подтверждения для CosmeticGuardian");
        message.setText(String.format(
                "Ваш код подтверждения: %s\n\nКод действителен в течение 15 минут.",
                code
        ));

        mailSender.send(message);
    }

    public void sendWelcomeEmail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Добро пожаловать в CosmeticGuardian!");
        message.setText("Спасибо за регистрацию! Ваш аккаунт успешно подтвержден.");

        mailSender.send(message);
    }
}