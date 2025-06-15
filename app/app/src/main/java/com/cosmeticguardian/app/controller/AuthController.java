package com.cosmeticguardian.app.controller;

import com.cosmeticguardian.app.dto.RegisterRequest;
import com.cosmeticguardian.app.dto.VerifyCodeRequest;
import com.cosmeticguardian.app.model.PendingRegistration;
import com.cosmeticguardian.app.model.User;
import com.cosmeticguardian.app.repo.PendingRegistrationRepository;
import com.cosmeticguardian.app.repo.UserRepository;
import com.cosmeticguardian.app.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"}, allowCredentials = "true")
public class AuthController {

    private static final int CODE_VALIDITY_MINUTES = 15;
    private static final int CODE_REQUEST_COOLDOWN_MINUTES = 1;

    private final PendingRegistrationRepository pendingRepo;
    private final UserRepository userRepo;
    private final EmailService emailService;

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startRegistration(@RequestBody @Valid RegisterRequest req) {
        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email уже используется");
        }

        Optional<PendingRegistration> existingReg = pendingRepo.findByEmail(req.getEmail());
        if (existingReg.isPresent()) {
            long minutesSinceLastRequest = ChronoUnit.MINUTES.between(
                    existingReg.get().getCreatedAt(),
                    LocalDateTime.now()
            );

            if (minutesSinceLastRequest < CODE_REQUEST_COOLDOWN_MINUTES) {
                throw new ResponseStatusException(
                        HttpStatus.TOO_MANY_REQUESTS,
                        String.format("Повторный запрос кода возможен через %d минут",
                                CODE_REQUEST_COOLDOWN_MINUTES - minutesSinceLastRequest)
                );
            }
            pendingRepo.delete(existingReg.get());
        }

        String code = String.format("%04d", new SecureRandom().nextInt(10_000));

        PendingRegistration reg = new PendingRegistration();
        reg.setEmail(req.getEmail());
        reg.setCode(code);
        reg.setCreatedAt(LocalDateTime.now());
        pendingRepo.save(reg);

        emailService.sendCode(req.getEmail(), code);
        log.info("Код подтверждения отправлен на email: {}", req.getEmail());

        return ResponseEntity.ok(Collections.singletonMap("message", "Код отправлен на почту"));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyCode(@RequestBody @Valid VerifyCodeRequest req) {
        PendingRegistration reg = pendingRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Регистрация не найдена"));

        if (reg.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(CODE_VALIDITY_MINUTES))) {
            pendingRepo.delete(reg);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Срок действия кода истек");
        }

        if (!reg.getCode().equals(req.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный код");
        }

        User user = new User();
        user.setEmail(reg.getEmail());
        user.setEmailConfirmed(true);
        userRepo.save(user);

        pendingRepo.delete(reg);

        emailService.sendWelcomeEmail(reg.getEmail());
        log.info("Пользователь успешно зарегистрирован: email={}", reg.getEmail());

        return ResponseEntity.ok(Collections.singletonMap("message", "Регистрация подтверждена"));
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@RequestParam String email) {
        boolean isAvailable = !userRepo.findByEmail(email).isPresent();
        return ResponseEntity.ok(Collections.singletonMap("available", isAvailable));
    }
}