package ru.mentee.power.crm.spring.repository;

import ru.mentee.power.crm.model.LeadStatusNew;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryLeadRepositoryStatus {
    private final Map<UUID, LeadStatusNew> store = new ConcurrentHashMap<>();

    public UUID addLeadStatus(LeadStatusNew leadStatus) {
        var id = UUID.randomUUID();
        store.put(id, leadStatus);
        return id;
    }
}