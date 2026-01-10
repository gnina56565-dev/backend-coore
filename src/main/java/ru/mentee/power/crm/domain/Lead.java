package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Lead(
        UUID id,
        String email,
        String phone,
        String company,
        String status
) {
    public Lead {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }
    }
}