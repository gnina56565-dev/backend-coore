package ru.mentee.power.crm.repository;

import ru.mentee.power.crm.domain.Lead;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository {
    Lead save(Lead lead);

    Optional<Lead> findById(UUID id);

    Optional<Lead> findByEmail(String email);

    List<Lead> findAll();

    void delete(UUID id);
}