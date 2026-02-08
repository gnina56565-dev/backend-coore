package ru.mentee.power.crm.spring;

import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

public class MockLeadService extends LeadService {
    private final List<Lead> mockLeads;

    public MockLeadService() {
        super(null);
        this.mockLeads = List.of(
                new Lead(UUID.randomUUID(), "test1@example.com", "Company A", LeadStatus.NEW),
                new Lead(UUID.randomUUID(), "test2@example.com", "Company B", LeadStatus.QUALIFIED)
        );
    }

    @Override
    public List<Lead> findAll() {
        return mockLeads;
    }
}