package ru.mentee.power.crm.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Lead {
    private UUID id;
    private String email;
    private String company;
    private LeadStatus status;

    public Lead(UUID id, String email, String company, LeadStatus status) {
        this.id = id;
        this.email = email;
        this.company = company;
        this.status = status;
    }
}
