package ru.mentee.power.crm.service;

import org.springframework.stereotype.Service;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.repository.LeadRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeadService {

    private final LeadRepository repository;
    public LeadService(LeadRepository repository) {
        this.repository = repository;
    }
    public Lead addLead(String email, String company, LeadStatus status) {
        Optional<Lead> existing = repository.findByEmail(email);
        if (existing.isPresent()) {
            throw new IllegalStateException("Lead with email already exists: " + email);
        }
        Lead lead = new Lead(
                UUID.randomUUID(),
                email,
                company,
                status
        );
        return repository.save(lead);
    }

    public List<Lead> findAll() {
        return repository.findAll();
    }

    public Optional<Lead> findById(UUID id) {
        return repository.findById(id);
    }

    public Optional<Lead> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}