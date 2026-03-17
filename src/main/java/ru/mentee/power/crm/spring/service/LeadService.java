package ru.mentee.power.crm.spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.exception.IllegalLeadStateException;
import ru.mentee.power.crm.model.CreateDealRequest;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadService {

    private final LeadRepository leadRepository;
    private final DealRepository dealRepository;
    private final LeadProcessor leadProcessor;

    @Transactional
    public Lead addLead(String email, String company, LeadStatus status) {
        Optional<Lead> existing = leadRepository.findByEmailNative(email);
        if (existing.isPresent()) {
            throw new IllegalStateException("Lead with email already exists: " + email);
        }
        Lead lead = new Lead(email, company, status);

        log.info("Saving lead: {}", lead);
        Lead saved = leadRepository.save(lead);
        log.info("Saved lead with ID: {}", saved.getId());

        return saved;
    }

    public List<Lead> findAll() {
        return leadRepository.findAll();
    }

    public List<Lead> findByStatus(LeadStatus status) {
        return leadRepository.findAll().stream()
                .filter(lead -> lead.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public Optional<Lead> findById(UUID id) {
        return leadRepository.findById(id);
    }

    public Lead update(UUID id, Lead updatedLead) {
        Lead newLead = leadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lead not found with id: " + id
                ));
        newLead.setEmail(updatedLead.getEmail());
        newLead.setCompany(updatedLead.getCompany());
        newLead.setStatus(updatedLead.getStatus());
        return leadRepository.save(newLead);
    }

    public void delete(UUID id) {
        leadRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lead not found with id: " + id
                ));
        leadRepository.deleteById(id);
    }

    public List<Lead> findLeads(String search, String status) {
        List<Lead> leads = leadRepository.findAll();
        return leads.stream()
                .filter(lead -> {
                    if (search == null || search.isBlank()) {
                        return true;
                    }
                    String lowerSearch = search.toLowerCase();
                    return lead.getEmail().toLowerCase().contains(lowerSearch);
                })
                .filter(lead -> {
                    if (status == null || status.isBlank()) {
                        return true;
                    }
                    return lead.getStatus() != null &&
                            lead.getStatus().name().equalsIgnoreCase(status);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void save(Lead lead) {
        leadRepository.save(lead);
    }

    public Optional<Lead> findByEmail(String email) {
        return leadRepository.findByEmail(email);
    }

    public List<Lead> findByStatuses(LeadStatus... statuses) {
        return leadRepository.findByStatusIn(List.of(statuses));
    }

    public Page<Lead> getFirstPage(int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                0,
                pageSize,
                Sort.by("createdAt").descending()
        );
        return leadRepository.findAll(pageRequest);
    }

    public Page<Lead> searchByCompany(String company, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return leadRepository.findByCompany(company, pageable);
    }

    @Transactional
    public int convertNewToContacted() {
        int updated = leadRepository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
        System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
        return updated;
    }

    @Transactional
    public int archiveOldLeads(LeadStatus status) {
        return leadRepository.deleteByStatusBulk(status);
    }

    @Transactional
    public Deal convertLeadToDeal(UUID leadId, CreateDealRequest request) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));
        if (lead.getStatus() != LeadStatus.QUALIFIED) {
            throw new IllegalLeadStateException(leadId, lead.getStatus());
        }
        Deal newDeal = new Deal(leadId, request.getAmount());
        Deal savedDeal = dealRepository.save(newDeal);

        lead.setStatus(LeadStatus.CONTACTED);
        leadRepository.save(lead);
        return savedDeal;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processLeads(List<UUID> ids) {
        for (UUID id : ids) {
            leadProcessor.processSingleLead(id);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleLead(UUID leadId) {
        Lead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));
        if (lead.getEmail().contains("throw-exception")) {
            throw new RuntimeException("Simulated error for lead: " + leadId);
        }
        lead.setStatus(LeadStatus.CONTACTED);
        leadRepository.save(lead);
    }
}