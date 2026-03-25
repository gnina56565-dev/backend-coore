package ru.mentee.power.crm.spring.repository;

import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.model.Company;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryLeadRepository implements LeadRepository {

    private final Map<UUID, Lead> storage = new ConcurrentHashMap<>();
    private final Map<String, UUID> emailIndex = new ConcurrentHashMap<>();

    public InMemoryLeadRepository() {
    }

    private void addLead(String email, Company company, LeadStatus status) {
        Lead lead = new Lead(UUID.randomUUID(), email, company, status);
        storage.put(lead.getId(), lead);
        emailIndex.put(email, lead.getId());
    }

    @Override
    public Lead save(Lead lead) {
        if (lead.getId() == null) {
            lead.setId(UUID.randomUUID());
        }
        storage.put(lead.getId(), lead);
        return lead;
    }

    @Override
    public Optional<Lead> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public Optional<Lead> findByEmail(String email) {
        UUID id = emailIndex.get(email);
        return id == null ? Optional.empty() : Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Lead> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(UUID id) {
        Lead lead = storage.remove(id);
        if (lead != null) {
            emailIndex.remove(lead.getEmail());
        }
    }
}
