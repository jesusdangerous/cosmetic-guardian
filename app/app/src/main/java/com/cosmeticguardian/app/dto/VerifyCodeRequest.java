package com.cosmeticguardian.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyCodeRequest {
    @NotBlank
    private String telegramId;

    @Pattern(regexp = "\\d{4}")
    private String code;
}
