package ru.mentee.power.crm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {
    @Query(value = "SELECT * FROM leads WHERE email = ?1", nativeQuery = true)
    public Optional<Lead> findByEmailNative(String email);

    @Query(value = "SELECT * FROM leads WHERE email = ?", nativeQuery = true)
    Optional<Lead> findByEmail(String email);

    List<Lead> findByStatus(LeadStatus status);

    List<Lead> findByCompany(String company);

    long countByStatus(LeadStatus status);

    boolean existsByEmail(String email);

    @Query("SELECT l FROM Lead l WHERE l.email LIKE CONCAT('%', :emailPart, '%')")
    List<Lead> findByEmailContaining(@Param("emailPart") String emailPart);

    List<Lead> findByStatusAndCompany(LeadStatus status, String company);
    List<Lead> findByStatusOrderByCreatedAtDesc(LeadStatus status);

    @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
    List<Lead> findByStatusIn(@Param("statuses") List<LeadStatus> statuses);

     @Query("SELECT l FROM Lead l WHERE l.createdAt > :date")
     List<Lead> findCreatedAfter(@Param("date") LocalDateTime date);

    @Query("SELECT l FROM Lead l WHERE l.company = :company ORDER BY l.createdAt DESC")
    List<Lead> findByCompanyOrderedByDate(@Param("company") String company);

    Page<Lead> findAll(Pageable pageable);

    Page<Lead> findByCompany(String company, Pageable pageable);

    @Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
    Page<Lead> findByStatusInPaged(@Param("statuses") List<LeadStatus> statuses, Pageable pageable);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE Lead l SET l.status = :newStatus WHERE l.status = :oldStatus")
    int updateStatusBulk(
            @Param("oldStatus") LeadStatus oldStatus,
            @Param("newStatus") LeadStatus newStatus
    );

     @Modifying
     @Query("DELETE FROM Lead l WHERE l.status = :status")
     int deleteByStatusBulk(@Param("status") LeadStatus status);
}