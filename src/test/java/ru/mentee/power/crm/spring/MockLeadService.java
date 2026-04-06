package ru.mentee.power.crm.spring;

import org.mockito.Mockito;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.spring.repository.CompanyRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.service.LeadProcessor;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;

public class MockLeadService extends LeadService {
    private final List<Lead> mockLeads;

    public MockLeadService() {
        super(
                Mockito.mock(LeadJpaRepository.class),
                (DealRepository) Mockito.mock(CompanyRepository.class),
                (LeadProcessor) Mockito.mock(DealRepository.class),
                (CompanyRepository) Mockito.mock(LeadProcessor.class)
        );

        Company companyA = new Company("Company A", "General");
        Company companyB = new Company("Company B", "General");

        this.mockLeads = List.of(
                createMockLead("test1@example.com", companyA, LeadStatus.NEW),
                createMockLead("test2@example.com", companyB, LeadStatus.QUALIFIED)
        );
    }

    private Lead createMockLead(String email, Company company, LeadStatus status) {
        Lead lead = new Lead(email, company, status);
        return lead;
    }

    @Override
    public List<Lead> findAll() {
        return mockLeads;
    }
}