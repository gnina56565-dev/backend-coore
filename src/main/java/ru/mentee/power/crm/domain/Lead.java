package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Lead(
        UUID id,
        Contact contact,
        String company,
        String status
) {
    public Lead {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        if (contact == null) {
            throw new IllegalArgumentException();
        }
        if (status == null) {
            throw new IllegalArgumentException();
        }
        if (!"NEW".equals(status) && !"QUALIFIED".equals(status) && !"CONVERTED".equals(status)) {
            throw new IllegalArgumentException();
        }
    }
}