package com.cosmeticguardian.app.repo;

import com.cosmeticguardian.app.model.PendingRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, String> {
}