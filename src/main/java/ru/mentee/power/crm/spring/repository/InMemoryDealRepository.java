package ru.mentee.power.crm.spring.repository;

import org.springframework.stereotype.Repository;
import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryDealRepository implements DealRepository {

    @Override
    public Deal save(Deal deal) {
        if (deal.getId() == null) {
            deal.setId(UUID.randomUUID());
        }
        storage.put(deal.getId(), deal);
        return deal;
    }

    private final Map<UUID, Deal> storage = new ConcurrentHashMap<>();


    @Override
    public Optional<Deal> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Deal> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Deal> findByStatus(DealStatus status) {
        return storage.values().stream()
                .filter(deal -> deal.getStatus() == status).collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        storage.remove(id);
    }
}