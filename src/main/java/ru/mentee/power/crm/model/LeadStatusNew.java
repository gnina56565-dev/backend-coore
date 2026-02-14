package ru.mentee.power.crm.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.List;

@Getter
@EqualsAndHashCode
public final class LeadStatusNew {
    private static final List<String> ALLOWED_VALUES = List.of(
            "NEW", "CONTACTED", "NEGOTIATION"
    );

    private final String value;

    private LeadStatusNew(String value) {
        if (!ALLOWED_VALUES.contains(value)) {
            throw new IllegalArgumentException("Invalid LeadStatus value: " + value);
        }
        this.value = value;
    }

    public static final LeadStatusNew NEW = new LeadStatusNew("NEW");
    public static final LeadStatusNew CONTACTED = new LeadStatusNew("CONTACTED");
    public static final LeadStatusNew NEGOTIATION = new LeadStatusNew("NEGOTIATION");

    public static List<LeadStatusNew> values() {
        return List.of(NEW, CONTACTED, NEGOTIATION);
    }

    public static LeadStatusNew fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return switch (value.toUpperCase()) {
            case "NEW" -> NEW;
            case "CONTACTED" -> CONTACTED;
            case "NEGOTIATION" -> NEGOTIATION;
            default -> throw new IllegalArgumentException("Unknown status: " + value);
        };
    }

    @Override
    public String toString() {
        return value;
    }
}