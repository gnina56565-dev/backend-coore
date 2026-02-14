package ru.mentee.power.crm.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Lead {
    private UUID id;
    private String email;
    private String company;
    private LeadStatus status;

    public Lead(UUID uuid, String mail, String existingCompany, LeadStatus leadStatus) {
    }
}
