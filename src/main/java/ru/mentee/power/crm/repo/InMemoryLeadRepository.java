package ru.mentee.power.crm.repo;

import ru.mentee.power.crm.domain.Lead;
import ru.mentee.power.crm.domain.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class InMemoryLeadRepository implements Repository<Lead> {
    private final List<Lead> storage = new ArrayList<>();

    @Override
    public void add(Lead lead) {
        if (!storage.contains(lead)) {
            storage.add(lead);
        }
    }

    @Override
    public Optional<Lead> findById(UUID id) {
        return storage.stream()
                .filter(lead -> lead.id().equals(id))
                .findFirst();
    }

    @Override
    public List<Lead> findAll() {
        return new ArrayList<>(storage);
    }

    @Override
    public void remove(UUID id) {
        storage.removeIf(lead -> lead.id().equals(id));
    }
}