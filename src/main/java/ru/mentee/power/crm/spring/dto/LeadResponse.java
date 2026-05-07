package ru.mentee.power.crm.spring.dto;

import ru.mentee.power.crm.model.LeadStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record LeadResponse(UUID id, String email, LeadStatus status, String company, LocalDateTime createdAt) {
}
