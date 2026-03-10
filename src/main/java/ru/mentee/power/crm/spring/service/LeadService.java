package ru.mentee.power.crm.spring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadService {
    private final LeadRepository repository;

    @Transactional
    public Lead addLead(String email, String company, LeadStatus status) {
        Optional<Lead> existing = repository.findByEmailNative(email);
        if (existing.isPresent()) {
            throw new IllegalStateException("Lead with email already exists: " + email);
        }
        Lead lead = new Lead(email, company, status);

        log.info("Saving lead: {}", lead);
        Lead saved = repository.save(lead);
        log.info("Saved lead with ID: {}", saved.getId());

        return saved;
    }

    public List<Lead> findAll() {
        return repository.findAll();
    }

    public List<Lead> findByStatus(LeadStatus status) {
        return repository.findAll().stream()
                .filter(lead -> lead.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public Optional<Lead> findById(UUID id) {
        return repository.findById(id);
    }


    public Lead update(UUID id, Lead updatedLead) {
        Lead newLead = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lead not found with id: " + id
                ));
        newLead.setEmail(updatedLead.getEmail());
        newLead.setCompany(updatedLead.getCompany());
        newLead.setStatus(updatedLead.getStatus());
        return repository.save(newLead);
    }
    public void delete(UUID id) {
        repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lead not found with id: " + id
                ));
        repository.deleteById(id);
    }

    public List<Lead> findLeads(String search, String status) {
        List<Lead> leads = repository.findAll();
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
                            lead.getStatus().name().equalsIgnoreCase(status); //для исправления работы listLeads
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void save(Lead lead) {
        repository.save(lead);
    }
    public Optional<Lead> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    public List<Lead> findByStatuses(LeadStatus... statuses) {
        return repository.findByStatusIn(List.of(statuses));
    }

    public Page<Lead> getFirstPage(int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                0,
                pageSize,
                Sort.by("createdAt").descending()
        );
        return repository.findAll(pageRequest);
    }

    public Page<Lead> searchByCompany(String company, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findByCompany(company, pageable);
    }

    @Transactional
    public int convertNewToContacted() {
        int updated = repository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
        System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
        return updated;
    }

     @Transactional
     public int archiveOldLeads(LeadStatus status) {
       return repository.deleteByStatusBulk(status);
     }
}
