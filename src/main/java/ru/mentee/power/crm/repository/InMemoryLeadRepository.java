package ru.mentee.power.crm.repository;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class InMemoryLeadRepository implements LeadJpaRepository {

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
        emailIndex.put(lead.getEmail(), lead.getId());
        return lead;
    }

    @Override
    public Optional<Lead> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    @Override
    public Optional<Lead> findByEmailIgnoreCase(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<Lead> findByEmailNative(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<Lead> findByEmail(String email) {
        UUID id = emailIndex.get(email);
        return id == null ? Optional.empty() : Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Lead> findByStatus(LeadStatus status) {
        return List.of();
    }

    @Override
    public List<Lead> findByCompany(Company company) {
        return List.of();
    }

    @Override
    public long countByStatus(LeadStatus status) {
        return 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return false;
    }

    @Override
    public List<Lead> findByEmailContaining(String emailPart) {
        return List.of();
    }

    @Override
    public List<Lead> findByStatusAndCompany(LeadStatus status, Company company) {
        return List.of();
    }

    @Override
    public List<Lead> findByStatusOrderByCreatedAtDesc(LeadStatus status) {
        return List.of();
    }

    @Override
    public List<Lead> findByStatusIn(List<LeadStatus> statuses) {
        return List.of();
    }

    @Override
    public List<Lead> findCreatedAfter(LocalDateTime date) {
        return List.of();
    }

    @Override
    public List<Lead> findByCompanyOrderedByDate(Company company) {
        return List.of();
    }

    @Override
    public Page<Lead> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public Page<Lead> findByCompany(Company company, Pageable pageable) {
        return null;
    }

    @Override
    public Page<Lead> findByStatusInPaged(List<LeadStatus> statuses, Pageable pageable) {
        return null;
    }

    @Override
    public int updateStatusBulk(LeadStatus oldStatus, LeadStatus newStatus) {
        return 0;
    }

    @Override
    public int deleteByStatusBulk(LeadStatus status) {
        return 0;
    }

    @Override
    public Optional<Lead> findByIdForUpdate(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<Lead> findByEmailForUpdate(String email) {
        return Optional.empty();
    }

    @Override
    public <S extends Lead> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public List<Lead> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public List<Lead> findAllById(Iterable<UUID> uuids) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(UUID uuid) {
        Lead lead = storage.remove(uuid);
        if (lead != null) {
            emailIndex.remove(lead.getEmail());
        }
    }

    @Override
    public void delete(Lead entity) {
        if (entity != null && entity.getId() != null) {
            Lead removed = storage.remove(entity.getId());
            if (removed != null) {
                emailIndex.remove(removed.getEmail());
            }
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends UUID> uuids) {

    }

    @Override
    public void deleteAll(Iterable<? extends Lead> entities) {

    }

    @Override
    public void deleteAll() {

    }


    @Override
    public void flush() {

    }

    @Override
    public <S extends Lead> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Lead> List<S> saveAllAndFlush(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public void deleteAllInBatch(Iterable<Lead> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<UUID> uuids) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Lead getOne(UUID uuid) {
        return null;
    }

    @Override
    public Lead getById(UUID uuid) {
        return null;
    }

    @Override
    public Lead getReferenceById(UUID uuid) {
        return null;
    }

    @Override
    public <S extends Lead> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Lead> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Lead> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Lead> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Lead> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Lead> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Lead, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    @Override
    public List<Lead> findAll(Sort sort) {
        return List.of();
    }
}
