package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Lead(
        UUID id,
        Contact contact,
        String company
        ) {
    public Lead {
        if (id == null) {
            throw new IllegalArgumentException();
        }
        if (contact == null) {
            throw new IllegalArgumentException();
        }
        if (company == null) {
            throw new IllegalArgumentException();
        }
    }
}
