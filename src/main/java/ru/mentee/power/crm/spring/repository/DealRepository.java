package ru.mentee.power.crm.spring.repository;

import ru.mentee.power.crm.domain.Deal;
import ru.mentee.power.crm.domain.DealStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DealRepository {
    void save(Deal deal);
    Optional<Deal> findById(UUID id);
    List<Deal> findAll();
    List<Deal> findByStatus(DealStatus status);
    void deleteById(UUID id);
}