// src/main/java/ru/mentee/power.crm/domain/Lead.java
package ru.mentee.power.crm.domain;

import java.util.Objects;
import java.util.UUID;

public record Lead(
        UUID id,
        String email,
        String company,
        ru.mentee.power.crm.domain.LeadStatus status
) {
    public Lead {
        Objects.requireNonNull(id);
        Objects.requireNonNull(email);
        Objects.requireNonNull(company);
        Objects.requireNonNull(status);
    }

    public static Lead of(String email, String company, ru.mentee.power.crm.domain.LeadStatus status) {
        return new Lead(UUID.randomUUID(), email, company, status);
    }
}