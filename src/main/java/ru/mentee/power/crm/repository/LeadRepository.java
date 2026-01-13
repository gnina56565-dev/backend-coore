package ru.mentee.power.crm.repository;

import ru.mentee.power.crm.domain.Lead;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LeadRepository {
    private final Map<UUID, Lead> storage = new HashMap<>();

    public void save(Lead lead) {
        storage.put(lead.id(), lead);
    }

    public Optional<Lead> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Lead> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(UUID id) {
        storage.remove(id);
    }

    public int size() {
        return storage.size();
    }
}