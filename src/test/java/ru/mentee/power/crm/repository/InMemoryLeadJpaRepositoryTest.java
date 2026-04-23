package ru.mentee.power.crm.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.mentee.power.crm.model.Lead;
import ru.mentee.power.crm.model.LeadStatus;
import ru.mentee.power.crm.model.Company;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryLeadJpaRepositoryTest {

  private InMemoryLeadRepository repository;
  private Lead lead1;
  private Lead lead2;
  private Company company1;
  private Company company2;

  @BeforeEach
  void setUp() {
    repository = new InMemoryLeadRepository();
    company1 = new Company("test1", "General");
    company2 = new Company("test2", "General");
    lead1 = new Lead(UUID.randomUUID(), "test1@example.com", company1, LeadStatus.NEW);
    lead2 = new Lead(UUID.randomUUID(), "test2@example.com", company2, LeadStatus.CONTACTED);
  }

  @Test
  void shouldSaveLead() {
    Lead savedLead = repository.save(lead1);
    assertThat(savedLead).isEqualTo(lead1);
    assertThat(repository.findById(lead1.getId())).contains(lead1);
    assertThat(repository.findByEmail(lead1.getEmail())).contains(lead1);
  }

  @Test
  void shouldFindLeadById() {
    repository.save(lead1);
    Optional<Lead> foundLead = repository.findById(lead1.getId());
    assertThat(foundLead).contains(lead1);
  }

  @Test
  void shouldReturnEmptyWhenLeadNotFoundById() {
    Optional<Lead> foundLead = repository.findById(UUID.randomUUID());
    assertThat(foundLead).isEmpty();
  }

  @Test
  void shouldFindLeadByEmail() {
    repository.save(lead1);
    Optional<Lead> foundLead = repository.findByEmail(lead1.getEmail());
    assertThat(foundLead).contains(lead1);
  }

  @Test
  void shouldReturnEmptyWhenLeadNotFoundByEmail() {
    Optional<Lead> foundLead = repository.findByEmail("nonexistent@example.com");
    assertThat(foundLead).isEmpty();
  }

  @Test
  void shouldFindAllLeads() {
    repository.save(lead1);
    repository.save(lead2);
    List<Lead> leads = repository.findAll();
    assertThat(leads).hasSize(2);
    assertThat(leads).contains(lead1, lead2);
  }

  @Test
  void shouldDeleteLead() {
    repository.save(lead1);
    repository.delete(lead1);
    assertThat(repository.findById(lead1.getId())).isEmpty();
    assertThat(repository.findByEmail(lead1.getEmail())).isEmpty();
  }

  @Test
  void shouldNotFailWhenDeletingNonExistentLead() {
    repository.delete(lead1);
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void shouldDeleteById() {
    repository.save(lead1);
    repository.deleteById(lead1.getId());
    assertThat(repository.findById(lead1.getId())).isEmpty();
    assertThat(repository.findByEmail(lead1.getEmail())).isEmpty();
  }

  @Test
  void shouldDeleteByIdForNonExistentId() {
    UUID nonExistentId = UUID.randomUUID();
    repository.deleteById(nonExistentId);
    assertThat(repository.findAll()).isEmpty();
  }

  @Test
  void shouldExistsByIdReturnFalse() {
    repository.save(lead1);
    boolean exists = repository.existsById(lead1.getId());
    assertThat(exists).isFalse();
  }

  @Test
  void shouldExistsByIdReturnFalseForNonExistentId() {
    boolean exists = repository.existsById(UUID.randomUUID());
    assertThat(exists).isFalse();
  }

  @Test
  void shouldFindByEmailNativeReturnEmpty() {
    repository.save(lead1);
    Optional<Lead> found = repository.findByEmailNative(lead1.getEmail());
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindByStatusReturnEmptyList() {
    repository.save(lead1);
    List<Lead> found = repository.findByStatus(LeadStatus.NEW);
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindByCompanyReturnEmptyList() {
    repository.save(lead1);
    List<Lead> found = repository.findByCompany(company1);
    assertThat(found).isEmpty();
  }

  @Test
  void shouldCountByStatusReturnZero() {
    repository.save(lead1);
    long count = repository.countByStatus(LeadStatus.NEW);
    assertThat(count).isZero();
  }

  @Test
  void shouldExistsByEmailReturnFalse() {
    repository.save(lead1);
    boolean exists = repository.existsByEmail(lead1.getEmail());
    assertThat(exists).isFalse();
  }

  @Test
  void shouldFindByEmailContainingReturnEmptyList() {
    repository.save(lead1);
    List<Lead> found = repository.findByEmailContaining("test");
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindByStatusAndCompanyReturnEmptyList() {
    repository.save(lead1);
    List<Lead> found = repository.findByStatusAndCompany(LeadStatus.NEW, company1);
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindByStatusOrderByCreatedAtDescReturnEmptyList() {
    repository.save(lead1);
    List<Lead> found = repository.findByStatusOrderByCreatedAtDesc(LeadStatus.NEW);
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindByStatusInReturnEmptyList() {
    repository.save(lead1);
    List<Lead> found = repository.findByStatusIn(List.of(LeadStatus.NEW, LeadStatus.CONTACTED));
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindCreatedAfterReturnEmptyList() {
    repository.save(lead1);
    LocalDateTime date = LocalDateTime.now().minusDays(1);
    List<Lead> found = repository.findCreatedAfter(date);
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindByCompanyOrderedByDateReturnEmptyList() {
    repository.save(lead1);
    List<Lead> found = repository.findByCompanyOrderedByDate(company1);
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindAllWithPageableReturnNull() {
    repository.save(lead1);
    Page<Lead> page = repository.findAll(PageRequest.of(0, 10));
    assertThat(page).isNull();
  }

  @Test
  void shouldFindByCompanyWithPageableReturnNull() {
    repository.save(lead1);
    Page<Lead> page = repository.findByCompany(company1, PageRequest.of(0, 10));
    assertThat(page).isNull();
  }

  @Test
  void shouldFindByStatusInPagedReturnNull() {
    repository.save(lead1);
    Page<Lead> page = repository.findByStatusInPaged(List.of(LeadStatus.NEW), PageRequest.of(0, 10));
    assertThat(page).isNull();
  }

  @Test
  void shouldUpdateStatusBulkReturnZero() {
    repository.save(lead1);
    int updated = repository.updateStatusBulk(LeadStatus.NEW, LeadStatus.CONTACTED);
    assertThat(updated).isZero();
  }

  @Test
  void shouldDeleteByStatusBulkReturnZero() {
    repository.save(lead1);
    int deleted = repository.deleteByStatusBulk(LeadStatus.NEW);
    assertThat(deleted).isZero();
  }

  @Test
  void shouldFindByIdForUpdateReturnEmpty() {
    repository.save(lead1);
    Optional<Lead> found = repository.findByIdForUpdate(lead1.getId());
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindByEmailForUpdateReturnEmpty() {
    repository.save(lead1);
    Optional<Lead> found = repository.findByEmailForUpdate(lead1.getEmail());
    assertThat(found).isEmpty();
  }

  @Test
  void shouldSaveAllReturnEmptyList() {
    List<Lead> leads = List.of(lead1, lead2);
    List<Lead> saved = repository.saveAll(leads);
    assertThat(saved).isEmpty();
  }

  @Test
  void shouldFindAllByIdReturnEmptyList() {
    repository.save(lead1);
    repository.save(lead2);
    List<Lead> found = repository.findAllById(List.of(lead1.getId(), lead2.getId()));
    assertThat(found).isEmpty();
  }

  @Test
  void shouldCountReturnZero() {
    repository.save(lead1);
    repository.save(lead2);
    long count = repository.count();
    assertThat(count).isZero();
  }

  @Test
  void shouldDeleteAllById() {
    repository.save(lead1);
    repository.save(lead2);
    repository.deleteAllById(List.of(lead1.getId(), lead2.getId()));
    assertThat(repository.findAll()).hasSize(2);
  }

  @Test
  void shouldDeleteAllIterable() {
    repository.save(lead1);
    repository.save(lead2);
    repository.deleteAll(List.of(lead1, lead2));
    assertThat(repository.findAll()).hasSize(2);
  }

  @Test
  void shouldDeleteAll() {
    repository.save(lead1);
    repository.save(lead2);
    repository.deleteAll();
    assertThat(repository.findAll()).hasSize(2);
  }

  @Test
  void shouldFlushDoNothing() {
    repository.save(lead1);
    repository.flush();
    assertThat(repository.findById(lead1.getId())).contains(lead1);
  }

  @Test
  void shouldDeleteAllInBatchWithIterable() {
    repository.save(lead1);
    repository.deleteAllInBatch(List.of(lead1));
    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldDeleteAllByIdInBatch() {
    repository.save(lead1);
    repository.deleteAllByIdInBatch(List.of(lead1.getId()));
    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldDeleteAllInBatch() {
    repository.save(lead1);
    repository.deleteAllInBatch();
    assertThat(repository.findAll()).hasSize(1);
  }

  @Test
  void shouldGetOneReturnNull() {
    repository.save(lead1);
    Lead result = repository.getOne(lead1.getId());
    assertThat(result).isNull();
  }

  @Test
  void shouldGetByIdReturnNull() {
    repository.save(lead1);
    Lead result = repository.getById(lead1.getId());
    assertThat(result).isNull();
  }

  @Test
  void shouldGetReferenceByIdReturnNull() {
    repository.save(lead1);
    Lead result = repository.getReferenceById(lead1.getId());
    assertThat(result).isNull();
  }

  @Test
  void shouldSaveAndFlushReturnNull() {
    Lead result = repository.saveAndFlush(lead1);
    assertThat(result).isNull();
  }

  @Test
  void shouldSaveAllAndFlushReturnEmptyList() {
    List<Lead> leads = List.of(lead1, lead2);
    List<Lead> result = repository.saveAllAndFlush(leads);
    assertThat(result).isEmpty();
  }

  @Test
  void shouldFindOneByExampleReturnEmpty() {
    repository.save(lead1);
    Example<Lead> example = Example.of(lead1);
    Optional<Lead> found = repository.findOne(example);
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindAllByExampleReturnEmptyList() {
    repository.save(lead1);
    Example<Lead> example = Example.of(lead1);
    List<Lead> found = repository.findAll(example);
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindAllByExampleWithSortReturnEmptyList() {
    repository.save(lead1);
    Example<Lead> example = Example.of(lead1);
    List<Lead> found = repository.findAll(example, Sort.by("email"));
    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindAllByExampleWithPageableReturnNull() {
    repository.save(lead1);
    Example<Lead> example = Example.of(lead1);
    Page<Lead> page = repository.findAll(example, PageRequest.of(0, 10));
    assertThat(page).isNull();
  }

  @Test
  void shouldCountByExampleReturnZero() {
    repository.save(lead1);
    Example<Lead> example = Example.of(lead1);
    long count = repository.count(example);
    assertThat(count).isZero();
  }

  @Test
  void shouldExistsByExampleReturnFalse() {
    repository.save(lead1);
    Example<Lead> example = Example.of(lead1);
    boolean exists = repository.exists(example);
    assertThat(exists).isFalse();
  }

  @Test
  void shouldFindByWithExampleAndFunctionReturnNull() {
    repository.save(lead1);
    Example<Lead> example = Example.of(lead1);
    Object result = repository.findBy(example, query -> query.first());
    assertThat(result).isNull();
  }

  @Test
  void shouldFindAllWithSortReturnEmptyList() {
    repository.save(lead1);
    List<Lead> found = repository.findAll(Sort.by("email"));
    assertThat(found).isEmpty();
  }
}
