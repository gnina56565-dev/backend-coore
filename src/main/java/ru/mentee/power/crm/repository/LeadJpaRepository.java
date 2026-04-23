package ru.mentee.power.crm.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mentee.power.crm.model.Company;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeadJpaRepository extends JpaRepository<Lead, UUID> {

	Optional<Lead> findByEmailIgnoreCase(String email);

	@Query(value = "SELECT * FROM leads WHERE email = ?1", nativeQuery = true)
	Optional<Lead> findByEmailNative(String email);

	@Query(value = "SELECT * FROM leads WHERE email = ?", nativeQuery = true)
	Optional<Lead> findByEmail(String email);

	List<Lead> findByStatus(LeadStatus status);

	List<Lead> findByCompany(Company company);

	long countByStatus(LeadStatus status);

	boolean existsByEmail(String email);

	@Query("SELECT l FROM Lead l WHERE l.email LIKE CONCAT('%', :emailPart, '%')")
	List<Lead> findByEmailContaining(@Param("emailPart") String emailPart);

	List<Lead> findByStatusAndCompany(LeadStatus status, Company company);

	List<Lead> findByStatusOrderByCreatedAtDesc(LeadStatus status);

	@Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
	List<Lead> findByStatusIn(@Param("statuses") List<LeadStatus> statuses);

	@Query("SELECT l FROM Lead l WHERE l.createdAt > :date")
	List<Lead> findCreatedAfter(@Param("date") LocalDateTime date);

	@Query("SELECT l FROM Lead l WHERE l.company = :company ORDER BY l.createdAt DESC")
	List<Lead> findByCompanyOrderedByDate(@Param("company") Company company);

	Page<Lead> findAll(Pageable pageable);

	Page<Lead> findByCompany(Company company, Pageable pageable);

	@Query("SELECT l FROM Lead l WHERE l.status IN :statuses")
	Page<Lead> findByStatusInPaged(@Param("statuses") List<LeadStatus> statuses, Pageable pageable);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE Lead l SET l.status = :newStatus WHERE l.status = :oldStatus")
	int updateStatusBulk(@Param("oldStatus") LeadStatus oldStatus, @Param("newStatus") LeadStatus newStatus);

	@Modifying
	@Query("DELETE FROM Lead l WHERE l.status = :status")
	int deleteByStatusBulk(@Param("status") LeadStatus status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT l FROM Lead l WHERE l.id = :id")
	Optional<Lead> findByIdForUpdate(@Param("id") UUID id);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT l FROM Lead l WHERE l.email = :email")
	Optional<Lead> findByEmailForUpdate(@Param("email") String email);
}
