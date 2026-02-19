package ru.mentee.power.crm.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data

public class Lead {
    private UUID id;
    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный формат email")
    @Size(min = 2, max = 100, message = "Имя должно быть от 2 до 100 символов")
    private String email;
    @NotBlank(message = "Компания обязательна")
    private String company;
    @NotNull(message = "Статус обязателен")
    private LeadStatus status;

    public Lead(UUID id, String email, String company, LeadStatus status) {
        this.id = id;
        this.email = email;
        this.company = company;
        this.status = status;
    }

    public Lead() {
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getCompany() {
        return company;
    }

    public LeadStatus getStatus() {
        return status;
    }
}