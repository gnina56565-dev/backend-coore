package ru.mentee.power.crm.spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadProcessor {
    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleLead(UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));

        if (lead.getCompany().contains("FAIL")) {
            throw new RuntimeException("Failure for lead: " + leadId);
        }

        Deal deal = new Deal(leadId, BigDecimal.TEN);
        dealRepository.save(deal);
    }
    private void logTx(String label) {
        boolean active = TransactionSynchronizationManager.isActualTransactionActive();
        String name = TransactionSynchronizationManager.getCurrentTransactionName();
        log.info(label, active, name);
    }
    @Transactional(propagation = Propagation.REQUIRED)
    public void methodRequired() {
        logTx("REQUIRED");
        leadRepository.save(new Lead());
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void methodRequiredNew() {
        logTx("REQUIRES_NEW");
        leadRepository.save(new Lead());
    }
    @Transactional(propagation = Propagation.MANDATORY)
    public void methodMandatory() {
        logTx("MANDATORY");
        leadRepository.save(new Lead());
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void methodReadCommitted() {
        logTx("READ_COMMITTED");
        leadRepository.save(new Lead());
    }
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void methodRepeatableRead() {
        logTx("REPEATABLE_READ");
        leadRepository.save(new Lead());
    }
}