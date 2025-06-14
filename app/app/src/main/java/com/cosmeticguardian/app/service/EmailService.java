package com.cosmeticguardian.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendCode(String toEmail, String code) {
        try {
            log.info("Отправка кода подтверждения на {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Ваш код подтверждения");
            message.setText("Код для подтверждения: " + code);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Ошибка отправки кода подтверждения", e);
            throw new RuntimeException("Не удалось отправить код подтверждения", e);
        }
    }

    public void sendWelcomeEmail(String toEmail) {
        try {
            log.info("Отправка приветственного письма на {}", toEmail);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Добро пожаловать!");
            message.setText("Благодарим вас за регистрацию в нашем сервисе!");
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Ошибка отправки приветственного письма", e);
        }
    }
}