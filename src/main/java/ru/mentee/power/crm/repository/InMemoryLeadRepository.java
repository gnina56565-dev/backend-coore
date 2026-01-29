package ru.mentee.power.crm.repository;

import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLeadRepository implements LeadRepository {

    private final Map<UUID, Lead> storage = new ConcurrentHashMap<>();
    private final Map<String, UUID> emailIndex = new ConcurrentHashMap<>();

    public InMemoryLeadRepository() {

    }

    private void addLead(String email, String company, LeadStatus status) {
        Lead lead = new Lead(UUID.randomUUID(), email, company, status);
        storage.put(lead.id(), lead);
        emailIndex.put(email, lead.id());
    }

    @Override
    public Lead save(Lead lead) {
        storage.put(lead.id(), lead);
        emailIndex.put(lead.email(), lead.id());
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
            emailIndex.remove(lead.email());
        }
    }
}