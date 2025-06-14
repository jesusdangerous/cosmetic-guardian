package com.cosmeticguardian.app.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String telegramId;
    private String email;
    private LocalDate birthDate;
    private boolean emailConfirmed;

    @ElementCollection
    private List<String> allergens;

    @ElementCollection
    private List<String> skinProblems;
}
