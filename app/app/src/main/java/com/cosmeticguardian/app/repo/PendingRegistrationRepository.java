package com.cosmeticguardian.app.repo;

import com.cosmeticguardian.app.model.PendingRegistration;
import jakarta.validation.constraints.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, String> {
    Optional<PendingRegistration> findByEmail(@Email String email);
}