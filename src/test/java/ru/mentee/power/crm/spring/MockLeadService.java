package ru.mentee.power.crm.spring;

import org.mockito.Mockito;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.service.LeadProcessor;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

public class MockLeadService extends LeadService {
    private final List<Lead> mockLeads;

    public MockLeadService() {
        super(
                Mockito.mock(LeadJpaRepository.class),
                Mockito.mock(DealRepository.class),
                Mockito.mock(LeadProcessor.class)
        );
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