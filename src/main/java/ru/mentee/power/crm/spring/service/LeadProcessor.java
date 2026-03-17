package ru.mentee.power.crm.spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadProcessor {
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;

    private void logTx(String label) {
        boolean active = TransactionSynchronizationManager.isActualTransactionActive();
        String name = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info("{}: active={}, name={}", label, active, name);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodRequired() {
        logTx("REQUIRED");
        Lead lead = new Lead("required+" + System.nanoTime() + "@test.com", "Required Corp", LeadStatus.NEW);
        leadRepository.save(lead);
        if (Boolean.parseBoolean(System.getProperty("test.fail.required", "false"))) {
            throw new RuntimeException("Forced rollback in REQUIRED");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodRequiredNew() {
        logTx("REQUIRES_NEW");
        Lead lead = new Lead("required-new+" + System.nanoTime() + "@test.com", "Requires New Corp", LeadStatus.NEW);
        leadRepository.save(lead);
        if (Boolean.parseBoolean(System.getProperty("test.fail.required-new", "false"))) {
            throw new RuntimeException("Forced rollback in REQUIRED_NEW");
        }
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void methodMandatory() {
        logTx("MANDATORY");
        Lead lead = new Lead("mandatory+" + System.nanoTime() + "@test.com", "Mandatory Corp", LeadStatus.NEW);
        leadRepository.save(lead);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void methodReadCommitted() {
        logTx("READ_COMMITTED");
        Lead lead = new Lead("read-committed+" + System.nanoTime() + "@test.com",
                "Read Committed Corp", LeadStatus.NEW);
        leadRepository.save(lead);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void methodRepeatableRead() {
        logTx("REPEATABLE_READ");
        Lead lead = new Lead("repeatable-read+" + System.nanoTime() + "@test.com",
                "Repeatable Read Corp", LeadStatus.NEW);
        leadRepository.save(lead);
        if (Boolean.parseBoolean(System.getProperty("test.fail.repeatable-read", "false"))) {
            throw new RuntimeException("Forced rollback in REPEATABLE-READ");
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleLead(UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

        if (lead.getEmail() != null && lead.getEmail().contains("throw-exception")) {
            throw new RuntimeException("Test failure for lead: " + leadId);
        }
        lead.setStatus(LeadStatus.CONTACTED);
        leadRepository.save(lead);
    }
}