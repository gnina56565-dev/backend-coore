package ru.mentee.power.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.mentee.power.crm.model.Lead;

import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
    @Query(value = "SELECT * FROM leads WHERE email = ?1", nativeQuery = true)
    public Optional<Lead> findByEmailNative(String email);

}