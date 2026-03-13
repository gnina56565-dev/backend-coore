package ru.mentee.power.crm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.service.LeadProcessor;
import ru.mentee.power.crm.spring.service.LeadService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
class LeadServiceIntegrationTest {

    @Autowired
    private LeadService leadService;

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private DealRepository dealRepository;

    @Autowired
    private LeadProcessor leadProcessor;


    @Test
    void convertLeadToDeal_shouldRollbackOnConstraintViolation() {
        Lead lead = new Lead("rollback+" + System.nanoTime() + "@test.com", "Corp", LeadStatus.NEW);
        leadRepository.save(lead);
        UUID leadId = lead.getId();
        assertThrows(NullPointerException.class, () ->
                leadService.convertLeadToDeal(leadId, null));
    }
    @Test
    void demonstrateSelfInvocationProblem() {
        Lead good = new Lead("good+" + System.nanoTime() + "@test.com", "Good", LeadStatus.NEW);
        Lead bad = new Lead("bad+" + System.nanoTime() + "@test.com", "Bad", LeadStatus.NEW);
        leadRepository.saveAll(List.of(good, bad));
        leadRepository.flush();
        assertThrows(
                RuntimeException.class,
                () -> leadService.processLeads(List.of(good.getId(), bad.getId()))
        );
        long dealsCount = dealRepository.findAll().size();
        assertEquals(1, dealsCount);
    }

    @Test
    void propagation_REQUIRED_reusesTransaction() {
        long before = leadRepository.count();
        leadProcessor.methodRequired();
        long after = leadRepository.count();
        assertEquals(before + 1, after);
    }

    @Test
    void propagation_REQUIRES_NEW_createsNewTransaction() {
        long before = leadRepository.count();
        leadProcessor.methodRequiredNew();
        long after = leadRepository.count();
        assertEquals(before + 1, after);
    }
    @Test
    void propagation_MANDATORY_worksWithTransaction() {
        assertDoesNotThrow(() -> leadProcessor.methodMandatory());
    }
    @Test
    void isolation_READ_COMMITTED_configured() {
        assertDoesNotThrow(() -> leadProcessor.methodReadCommitted());
    }

    @Test
    void isolation_REPEATABLE_READ_configured() {
        assertDoesNotThrow(() -> leadProcessor.methodRepeatableRead());
    }
}
