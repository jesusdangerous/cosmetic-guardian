package com.cosmeticguardian.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyCodeRequest {
    @NotBlank(message = "Email обязателен")
    private String email;

    @NotBlank(message = "Код обязателен")
    @Pattern(regexp = "\\d{4}", message = "Код должен состоять из 4 цифр")
    private String code;
}