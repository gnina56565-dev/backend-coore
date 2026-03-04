package ru.mentee.power.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "leads")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2  до 100 символов")
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    @NotBlank(message = "Компания обязательна")
    @Column(nullable = false, unique = true)
    private String company;
    @NotNull(message = "Статус обязателен")
    @Column(nullable = false, unique = true)
    private LeadStatus status;

}