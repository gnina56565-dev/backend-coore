package ru.mentee.power.crm.application.dto;

import java.time.Instant;
import java.util.UUID;

public record InviteeResponse(UUID id, String email, String firstName, String status, Instant createdAt) {
}
