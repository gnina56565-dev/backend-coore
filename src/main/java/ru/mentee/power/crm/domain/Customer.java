package ru.mentee.power.crm.domain;

import java.util.UUID;

public record Customer(
        UUID id,
        Contact contact,
        Address billingAddress,
        String loyaltyTier

) {
    public Customer {
        if (!"BRONZE".equals(loyaltyTier) && !"SILVER".equals(loyaltyTier) && !"GOLD".equals(loyaltyTier)) {
            throw new IllegalArgumentException();
        }
    }
}