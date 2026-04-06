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
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.CreateDealRequest;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadJpaRepository;
import ru.mentee.power.crm.spring.repository.CompanyRepository;
import ru.mentee.power.crm.spring.repository.DealRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeadService {

    private final LeadJpaRepository leadJpaRepository;
    private final DealRepository dealRepository;
    private final LeadProcessor leadProcessor;
    private final CompanyRepository companyRepository;

    @Transactional
    public void addLead(String email, String companyNameStr, LeadStatus status) {
        Company company = companyRepository.findByName(companyNameStr)
                .orElseGet(() -> {
                    Company newCompany = new Company(companyNameStr, null);
                    return companyRepository.save(newCompany);
                });

        Lead lead = new Lead(email, company, status);
        leadJpaRepository.save(lead);
    }

    public List<Lead> findAll() {
        return leadJpaRepository.findAll();
    }

    public List<Lead> findByStatus(LeadStatus status) {
        return leadJpaRepository.findAll().stream()
                .filter(lead -> lead.getStatus().equals(status))
                .collect(Collectors.toList());
    }

    public Optional<Lead> findById(UUID id) {
        return leadJpaRepository.findById(id);
    }

    public Lead update(UUID id, Lead updatedLead) {
        Lead newLead = leadJpaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lead not found with id: " + id
                ));
        newLead.setEmail(updatedLead.getEmail());
        newLead.setCompany(updatedLead.getCompany());
        newLead.setStatus(updatedLead.getStatus());
        return leadJpaRepository.save(newLead);
    }

    public void delete(UUID id) {
        leadJpaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Lead not found with id: " + id
                ));
        leadJpaRepository.deleteById(id);
    }

    public List<Lead> findLeads(String search, String status) {
        List<Lead> leads = leadJpaRepository.findAll();
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
        if (lead.getCompany() == null && lead.getCompanyName() != null) {
            String companyName = lead.getCompanyName().trim();
            Company company = companyRepository.findByName(companyName)
                    .orElseGet(() -> {
                        Company newCompany = new Company(companyName, null);
                        return companyRepository.save(newCompany);
                    });

            lead.setCompany(company);
        } else if (lead.getCompany() != null && lead.getCompany().getId() == null) {
            String companyName = lead.getCompany().getName();
            Company company = companyRepository.findByName(companyName)
                    .orElseGet(() -> companyRepository.save(lead.getCompany()));
            lead.setCompany(company);
        }
        leadJpaRepository.save(lead);
    }

    public Optional<Lead> findByEmail(String email) {
        return leadJpaRepository.findByEmail(email);
    }

    public List<Lead> findByStatuses(LeadStatus... statuses) {
        return leadJpaRepository.findByStatusIn(List.of(statuses));
    }

    public Page<Lead> getFirstPage(int pageSize) {
        PageRequest pageRequest = PageRequest.of(
                0,
                pageSize,
                Sort.by("createdAt").descending()
        );
        return leadJpaRepository.findAll(pageRequest);
    }

    public Page<Lead> searchByCompany(Company company, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return leadJpaRepository.findByCompany(company, pageable);
    }

    @Transactional
    public int convertNewToContacted() {
        int updated = leadJpaRepository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
        System.out.printf("Converted %d leads from NEW to CONTACTED%n", updated);
        return updated;
    }

    @Transactional
    public int archiveOldLeads(LeadStatus status) {
        return leadJpaRepository.deleteByStatusBulk(status);
    }

    @Transactional
    public Deal convertLeadToDeal(UUID leadId, CreateDealRequest request) {
        Lead lead = leadJpaRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));
        if (lead.getStatus() != LeadStatus.QUALIFIED) {
            throw new IllegalLeadStateException(leadId, lead.getStatus());
        }
        Deal newDeal = new Deal(leadId, request.getAmount());
        Deal savedDeal = dealRepository.save(newDeal);

        lead.setStatus(LeadStatus.CONTACTED);
        leadJpaRepository.save(lead);
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
        Lead lead = leadJpaRepository.findById(leadId)
                .orElseThrow(() -> new IllegalArgumentException("Lead not found: " + leadId));
        if (lead.getEmail().contains("throw-exception")) {
            throw new RuntimeException("Simulated error for lead: " + leadId);
        }
        lead.setStatus(LeadStatus.CONTACTED);
        leadJpaRepository.save(lead);
    }


}