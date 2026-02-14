package ru.mentee.power.crm.spring.service;

import ru.mentee.power.crm.model.LeadStatusNew;
import ru.mentee.power.crm.spring.repository.InMemoryLeadRepositoryStatus;

import java.util.List;
import java.util.UUID;

public class LeadStatusService {
    private final InMemoryLeadRepositoryStatus inMemoryLeadRepositoryStatus;

    public LeadStatusService(InMemoryLeadRepositoryStatus inMemoryLeadRepositoryStatus) {
        this.inMemoryLeadRepositoryStatus = inMemoryLeadRepositoryStatus;
    }

    public UUID leadAddStatus(LeadStatusNew leadStatus){
        var leadId = inMemoryLeadRepositoryStatus.addLeadStatus(leadStatus);
        return leadId;
    }

    public List<LeadStatusNew> getAllStatuses() {
        return LeadStatusNew.values();
    }
}