package ru.mentee.power.crm.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;
import ru.mentee.power.crm.spring.service.LeadProcessor;
import ru.mentee.power.crm.spring.service.LeadService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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
        Lead lead = new Lead("rollback@test.com", "Rollback Corp", LeadStatus.NEW);
        leadRepository.save(lead);
        UUID leadId = lead.getId();
        LeadStatus originalStatus = lead.getStatus();
        LocalDateTime originalCreatedAt = lead.getCreatedAt();
        int dealsCountBefore = dealRepository.findAll().size();

        assertThrows(Exception.class, () ->
                leadService.convertLeadToDeal(leadId, null)
        );
        int dealsCountAfter = dealRepository.findAll().size();

        assertEquals(dealsCountBefore, dealsCountAfter, "Deal was created despite error");
        Lead leadAfter = leadRepository.findById(leadId).orElseThrow();
        assertEquals(originalStatus, leadAfter.getStatus());
        assertEquals(originalCreatedAt, leadAfter.getCreatedAt());
    }
    @Test
    void demonstrateSelfInvocationProblem() {
        Lead leadFirst = new Lead("ex@test.com", "Rollback Corp", LeadStatus.NEW);
        Lead leadSecond = new Lead("exam@test.com", "Rollback Corp", LeadStatus.NEW);
        Lead leadThird = new Lead("example@test.com", "Rollback Corp", LeadStatus.NEW);
        List<UUID> ids = List.of(leadFirst.getId(), leadSecond.getId(), leadThird.getId());
        int dealsBefore = dealRepository.findAll().size();
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> leadService.processLeads(ids)
        );
        assertTrue(exception.getMessage().contains("Failure"));
        int dealsAfter = dealRepository.findAll().size();
        assertEquals(dealsBefore, dealsAfter);
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
